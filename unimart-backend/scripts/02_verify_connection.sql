-- 02_verify_connection.sql
-- Run inside the "UniMart Local" Workbench connection (connecting as
-- unimart_app, NOT root) to confirm access. See Guide 04, section 4.

SELECT VERSION();
USE unimart;
SELECT DATABASE();
SHOW TABLES;

-- After the backend has run its Flyway migrations (Guide 05), you should
-- see: flyway_schema_history, users, categories, listings, listing_images,
-- conversations, messages, orders, payments, reviews, notifications.

-- Safe, non-destructive inspection example (Guide 04, section 6):
-- SELECT id, university_email, role, created_at
-- FROM users
-- ORDER BY created_at DESC
-- LIMIT 20;
