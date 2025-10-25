# 🌀 MarketFlow-Lambda

MarketFlow-Lambda is a **big data streaming pipeline** built with **Kafka**, **Spark Structured Streaming**, and **HDFS**.  
It simulates stock price streams, processes them with Spark, and stores them in HDFS for further analytics.

---

## Architecture Overview

## Usage
```commandline
docker-compose up -d

./build-project.sh

./run-producer.sh 

./run-kafka-to-hdfs.sh 

./run-hdfs-to-cassandra.sh
```

