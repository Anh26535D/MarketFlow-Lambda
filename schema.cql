CREATE KEYSPACE IF NOT EXISTS marketflow
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS marketflow.customer_purchase_history (
    customerId TEXT,
    timestamp TEXT,
    productId TEXT,
    productName TEXT,
    category TEXT,
    brand TEXT,
    price DOUBLE,
    quantity INT,
    totalAmount DOUBLE,
    paymentMethod TEXT,
    region TEXT,
    sourceSystem TEXT,
    PRIMARY KEY (customerId, productId, timestamp)
) WITH CLUSTERING ORDER BY (productId ASC, timestamp DESC);
