# 🌀 MarketFlow-Lambda

MarketFlow-Lambda is a **big data streaming pipeline** built with **Kafka**, **Spark Structured Streaming**, and **HDFS**.  
It simulates stock price streams, processes them with Spark, and stores them in HDFS for further analytics.

---

## Prerequisites
1. Download datasets from src-links.txt and place them in the `datasets/` directory.
datasets/
- hm_fashion/
  - articles.csv
  - customers.csv
  - transactions.csv
- olist/
- retal/

## Architecture Overview

## Usage
```commandline
docker-compose up -d

./build-project.sh

./run-producer.sh 

./run-kafka-to-hdfs.sh 

./run-hdfs-to-cassandra.sh

./run-speed-layer.sh```

