CREATE KEYSPACE IF NOT EXISTS marketflow
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

CREATE TABLE IF NOT EXISTS marketflow.stock_prices (
    symbol TEXT,
    date TIMESTAMP,
    negotiated_volume BIGINT,
    matched_volume BIGINT,
    negotiated_value DOUBLE,
    matched_value DOUBLE,
    open_price DOUBLE,
    high_price DOUBLE,
    low_price DOUBLE,
    close_price DOUBLE,
    adjusted_price DOUBLE,
    change DOUBLE,
    PRIMARY KEY (symbol, date)
);
