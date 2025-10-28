#!/bin/bash
set -e

JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
MAIN_CLASS="edu.hust.marketflow.batch.HdfsToCustomerPurchaseHistory"
SPARK_MASTER_URL="spark://spark-master:7077"

echo "[1/5] Verifying JAR..."
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found. Please build the project first using ./build_project.sh"
  exit 1
fi

echo "[2/5] Copying JAR into Spark master container..."
docker cp "$JAR_PATH" "$SPARK_CONTAINER:$CONTAINER_JAR_PATH"

echo "[3/5] Ensure Cassandra is ready and apply schema..."
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

echo "[4/5] Submitting Spark Batch job to cluster..."
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
  echo "❌ spark-submit inside container returned exit code $SPARK_EXIT"
  echo "👉 Check Spark logs: docker logs $SPARK_CONTAINER"
  exit $SPARK_EXIT
fi

echo "[5/5] ✅ Spark Batch job completed!"
echo "👉 Check Spark Web UI at: http://localhost:8090"
echo "👉 Check Cassandra data via: docker exec -it cassandra cqlsh"
