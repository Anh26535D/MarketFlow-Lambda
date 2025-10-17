#!/bin/bash
set -e

JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
MAIN_CLASS="edu.hust.marketflow.consumer.SparkKafkaToHdfs"
SPARK_MASTER_URL="spark://spark-master:7077"

echo "[1/4] Checking fat JAR with Maven Shade Plugin..."

if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found at $JAR_PATH"
  exit 1
fi

echo "[2/4] Copying JAR into Spark master container..."
cp "$JAR_PATH" ./spark-jobs

echo "[3/4] Submitting Spark Streaming job to cluster..."
docker exec -it "$SPARK_CONTAINER" bash -c "
  /spark/bin/spark-submit \
    --class $MAIN_CLASS \
    --master $SPARK_MASTER_URL \
    $CONTAINER_JAR_PATH
"

echo "[4/4] Spark Streaming job is now running!"
echo "👉 Check Spark Web UI at: http://localhost:8090"