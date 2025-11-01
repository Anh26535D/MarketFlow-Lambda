-- ==========================================
--  Cassandra Schema for MarketFlow Speed Layer
-- ==========================================

-- Create keyspace
CREATE KEYSPACE IF NOT EXISTS marketflow
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

-- ==========================================
-- 1️⃣ Total Revenue (5min & 1hour)
-- ==========================================
CREATE TABLE IF NOT EXISTS marketflow.total_revenue (
    window_type text,                -- '5min' or '1hour'
    window_start timestamp,
    window_end timestamp,
    total_revenue double,
    transactions bigint,
    unique_customers bigint,
    update_time timestamp,
    PRIMARY KEY ((window_type), window_start)
);

-- ==========================================
-- 2️⃣ Region Revenue
-- ==========================================
CREATE TABLE IF NOT EXISTS marketflow.region_revenue (
    window_type text,
    window_start timestamp,
    window_end timestamp,
    region text,
    total_revenue double,
    update_time timestamp,
    PRIMARY KEY ((window_type), window_start, region)
);

-- ==========================================
-- 3️⃣ Top Products
-- ==========================================
CREATE TABLE IF NOT EXISTS marketflow.top_products (
    window_type text,
    window_start timestamp,
    window_end timestamp,
    productid text,
    productname text,
    total_quantity bigint,
    total_revenue double,
    update_time timestamp,
    PRIMARY KEY ((window_type, window_start), total_revenue, productid)
) WITH CLUSTERING ORDER BY (total_revenue DESC);

-- ==========================================
-- 4️⃣ Top Customers
-- ==========================================
CREATE TABLE IF NOT EXISTS marketflow.top_customers (
    window_type text,
    window_start timestamp,
    window_end timestamp,
    customerid text,
    customersegment text,
    total_spent double,
    update_time timestamp,
    PRIMARY KEY ((window_type, window_start), total_spent, customerid)
) WITH CLUSTERING ORDER BY (total_spent DESC);
