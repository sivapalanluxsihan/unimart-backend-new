-- 01_create_schema_and_user.sql
-- Run this in MySQL Workbench (or `mysql -u root -p`) as an administrative
-- account. See Guide 04, section 3.
--
-- IMPORTANT: replace 'replace-with-a-strong-local-password' below before
-- running, and use that same password as DB_PASSWORD in your IntelliJ run
-- configuration / .env file (see Guide 05 and README.md).

CREATE DATABASE IF NOT EXISTS unimart
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

CREATE USER IF NOT EXISTS 'unimart_app'@'localhost'
    IDENTIFIED BY 'replace-with-a-strong-local-password';

GRANT SELECT, INSERT, UPDATE, DELETE,
      CREATE, ALTER, INDEX, REFERENCES
ON unimart.* TO 'unimart_app'@'localhost';

FLUSH PRIVILEGES;

SHOW GRANTS FOR 'unimart_app'@'localhost';
