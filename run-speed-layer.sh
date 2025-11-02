#!/bin/bash
set -e

# -------------------------------------
# Configuration
# -------------------------------------
JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
MAIN_CLASS="edu.hust.marketflow.speed.SaveLiveDataToCassandra"
SPARK_MASTER_URL="spark://spark-master:7077"

# Cassandra defaults
CASSANDRA_CONTAINER="cassandra"
CASSANDRA_USER="cassandra"
CASSANDRA_PASS="cassandra"
CASSANDRA_SCHEMA_FILE="schemaDataReport.cql"

# HDFS checkpoint directory
CHECKPOINT_DIR="/marketflow/checkpoints/speedlayer"

# -------------------------------------
# Step 1: Verify and copy JAR
# -------------------------------------
echo "[1/5] Verifying JAR..."
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found. Build first using: ./build_project.sh"
  exit 1
fi

echo "[2/5] Copying JAR to Spark master..."
docker cp "$JAR_PATH" "$SPARK_CONTAINER:$CONTAINER_JAR_PATH"

# -------------------------------------
# Step 2: Apply Cassandra schema
# -------------------------------------
echo "[3/5] Applying Cassandra schema..."
MAX_ATTEMPTS=30
ATTEMPT=0
until docker exec cassandra cqlsh --username cassandra --password cassandra -e "SELECT release_version FROM system.local;" >/dev/null 2>&1; do
  ATTEMPT=$((ATTEMPT+1))
  if [ "$ATTEMPT" -ge "$MAX_ATTEMPTS" ]; then
    echo "❌ Cassandra did not become ready after $((MAX_ATTEMPTS*5)) seconds. Check 'docker logs cassandra' for details."
    exit 1
  fi
  echo "→ Waiting for Cassandra to start (attempt $ATTEMPT/$MAX_ATTEMPTS)..."
  sleep 5
done

echo "→ Cassandra is ready. Applying schema..."
docker exec cassandra cqlsh --username cassandra --password cassandra -f schemaDataReport.cql


# -------------------------------------
# Step 3: Prepare HDFS checkpoint
# -------------------------------------
echo "[4/5] Ensuring HDFS checkpoint directory..."
docker exec namenode hdfs dfs -mkdir -p "$CHECKPOINT_DIR" || true
docker exec namenode hdfs dfs -chmod -R 777 "$CHECKPOINT_DIR" || true

# -------------------------------------
# Step 4: Run Spark job
# -------------------------------------
echo "[5/5] 🚀 Running Spark Speed Layer job..."
docker exec -it "$SPARK_CONTAINER" bash -lc "
  /opt/spark/bin/spark-submit \
    --class $MAIN_CLASS \
    --master $SPARK_MASTER_URL \
    --conf spark.cassandra.connection.host=cassandra \
    --conf spark.cassandra.connection.port=9042 \
    $CONTAINER_JAR_PATH
"

echo "✅ Spark Speed Layer job started successfully!"
