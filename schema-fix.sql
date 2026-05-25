-- Run this in MySQL if transactions still fail after restart
USE skye;

ALTER TABLE transactions DROP COLUMN IF EXISTS category;
ALTER TABLE transactions DROP COLUMN IF EXISTS type;

-- If problems continue, reset tables completely:
-- DROP TABLE IF EXISTS transactions;
-- DROP TABLE IF EXISTS categories;
-- DROP TABLE IF EXISTS savings_goals;
-- Then restart the Spring Boot app.
