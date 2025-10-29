CREATE KEYSPACE IF NOT EXISTS marketflow
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS marketflow.customer_purchase_history (
    customer_id TEXT,
    timestamp TEXT,
    product_id TEXT,
    product_name TEXT,
    category TEXT,
    brand TEXT,
    price DOUBLE,
    quantity INT,
    total_amount DOUBLE,
    payment_method TEXT,
    region TEXT,
    source_system TEXT,
    PRIMARY KEY (customer_id, product_id, timestamp)
) WITH CLUSTERING ORDER BY (product_id ASC, timestamp DESC);
