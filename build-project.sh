#!/bin/bash
# =======================================
# MarketFlow-Lambda: Build Maven Project
# =======================================

PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

cd "$PROJECT_DIR" || exit 1

echo "🧩 Building MarketFlow-Lambda project..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
  echo "✅ Build successful! JAR available in target/"
else
  echo "❌ Build failed. Check errors above."
  exit 1
fi
