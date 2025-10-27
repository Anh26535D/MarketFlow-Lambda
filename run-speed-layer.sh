#!/bin/bash

JAR_NAME="MarketFlow-Lambda-1.0-SNAPSHOT-shaded.jar"
JAR_PATH="target/$JAR_NAME"
SPARK_CONTAINER="spark-master"
CONTAINER_JAR_PATH="/opt/spark-jobs/$JAR_NAME"
MAIN_CLASS="edu.hust.marketflow.speed.SaveLiveDataToCassandra"
SPARK_MASTER_URL="spark://spark-master:7077"

echo "[1/3] Copying JAR into Spark master..."
docker cp "$JAR_PATH" "$SPARK_CONTAINER:$CONTAINER_JAR_PATH"

echo "[2/3] Running Spark job..."
docker exec -it $SPARK_CONTAINER bash -c "
  /opt/spark/bin/spark-submit \
    --class $MAIN_CLASS \
    --master $SPARK_MASTER_URL \
    --deploy-mode client \
    --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.0,com.datastax.spark:spark-cassandra-connector_2.12:3.5.0 \
    $CONTAINER_JAR_PATH
"

echo "[3/3] Job completed (check logs in Spark UI)"
