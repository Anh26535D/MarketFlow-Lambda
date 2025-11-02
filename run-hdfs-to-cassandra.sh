#!/bin/bash
set -e

JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
SPARK_MASTER_URL="spark://spark-master:7077"

# --- List of batch job classes ---
BATCH_JOBS=(
  "edu.hust.marketflow.batch.HdfsToCustomerPurchaseHistory"
  "edu.hust.marketflow.batch.HdfsToHourlyPurchaseRevenue"
)

echo "[1/6] Verifying JAR..."
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found. Please build the project first using ./build_project.sh"
  exit 1
fi

echo "[2/6] Copying JAR into Spark master container..."
docker cp "$JAR_PATH" "$SPARK_CONTAINER:$CONTAINER_JAR_PATH"

echo "[3/6] Ensure Cassandra is ready and apply schema..."
if command -v docker-compose >/dev/null 2>&1; then
  echo "→ Starting docker-compose services (if not already up)..."
  docker-compose up -d || true
fi

# Wait for Cassandra to accept CQL connections
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
docker exec cassandra cqlsh --username cassandra --password cassandra -f schema.cql

# give Cassandra a moment to propagate schema metadata
echo "→ Waiting briefly for schema propagation..."
sleep 5

# --- Run all batch jobs sequentially ---
JOB_INDEX=1
for MAIN_CLASS in "${BATCH_JOBS[@]}"; do
  echo "[4/$((4+${#BATCH_JOBS[@]}))] 🚀 Submitting Spark Batch Job #$JOB_INDEX: $MAIN_CLASS ..."
  docker exec "$SPARK_CONTAINER" bash -c "
    /opt/spark/bin/spark-submit \
      --class $MAIN_CLASS \
      --master $SPARK_MASTER_URL \
      --conf spark.cassandra.connection.host=cassandra \
      --conf spark.cassandra.connection.port=9042 \
      --conf spark.sql.extensions=com.datastax.spark.connector.CassandraSparkExtensions \
      $CONTAINER_JAR_PATH
  "
  SPARK_EXIT=$?
  if [ $SPARK_EXIT -ne 0 ]; then
    echo "❌ Job $MAIN_CLASS failed with exit code $SPARK_EXIT"
    echo "👉 Check Spark logs: docker logs $SPARK_CONTAINER"
    exit $SPARK_EXIT
  fi
  echo "✅ Job #$JOB_INDEX ($MAIN_CLASS) completed successfully!"
  JOB_INDEX=$((JOB_INDEX+1))
done

echo "[${#BATCH_JOBS[@]}/6] ✅ All Spark Batch jobs completed successfully!"
echo "👉 Check Spark Web UI: http://localhost:8090"
echo "👉 Check Cassandra data via: docker exec -it cassandra cqlsh"
