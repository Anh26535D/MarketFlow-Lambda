#!/bin/bash
# ==============================================
# Run Kafka Producer on local machine
# ==============================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
JAR_PATH="$PROJECT_DIR/target/marketflow-lambda-1.0-SNAPSHOT-shaded.jar"
MAIN_CLASS="edu.hust.marketflow.producer.KafkaPriceProducer"

# Check if built JAR exists
if [ ! -f "$JAR_PATH" ]; then
  echo "❌ JAR not found. Please build the project first using ./build_project.sh"
  exit 1
fi

echo "🚀 Running Kafka Producer locally..."
java -cp "$JAR_PATH" "$MAIN_CLASS"
