#!/bin/bash
# ------------------------------------------------------------
# Run data integration pipeline
# Directory structure:
# MarketFlow-Lambda/
# ├── docker-compose.yml
# └── data_integration/
#     ├── merge_datasets.py
#     ├── requirements.txt
#     ├── input/
#     └── output/
# ------------------------------------------------------------

echo "🚀 Starting Data Integration..."

# Move into the project directory (in case script is run elsewhere)
cd "$(dirname "$0")"

# Remove old container if exists
docker compose rm -sf data-integration

# Build and run the container
docker compose up --build data-integration

echo "✅ Data integration completed."
