#!/bin/bash
set -e

JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
MAIN_CLASS="edu.hust.marketflow.consumer.SparkKafkaToHdfs"
SPARK_MASTER_URL="spark://spark-master:7077"

echo "[1/5] Verifying JAR..."
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found. Please build the project first using ./build_project.sh"
  exit 1
fi

echo "[2/5] Copying JAR into Spark master container..."
cp "$JAR_PATH" ./spark-jobs

# TODO: consider another way for permissions
echo "[3/5] Change HDFS permissions..."
docker exec -it namenode bash -c "
  hdfs dfs -chmod 777 /
"

echo "[4/5] Submitting Spark Streaming job to cluster..."
docker exec -it "$SPARK_CONTAINER" bash -c "
  /opt/spark/bin/spark-submit \
    --class $MAIN_CLASS \
    --master $SPARK_MASTER_URL \
    $CONTAINER_JAR_PATH
"

echo "[5/5] Spark Streaming job is now running!"
echo "👉 Check Spark Web UI at: http://localhost:8090"