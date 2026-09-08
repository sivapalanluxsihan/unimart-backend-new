-- 04_seed_review_test_data.sql
-- Guide 07 prerequisites: "A seeded category, users and one completed
-- order for review testing." There is no order-creation endpoint yet in
-- this lab (that belongs to the orders/payments feature), so a completed
-- order is inserted directly here for testing the Review CRUD.
--
-- Run this in MySQL Workbench, connected as unimart_app, AFTER:
--   1. You've registered a seller and a buyer via POST /auth/register
--      (see Postman collection, "Auth" folder).
--   2. The seller has created at least one listing via POST /listings
--      (see Postman collection, "Listings" folder).

USE unimart;

-- 1. Make sure at least one active category exists (id referenced by
--    Postman's `category_id` variable, default 1).
INSERT INTO categories (name, active)
SELECT 'Textbooks', TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = 'Textbooks');

-- 2. Look up the ids you'll need below.
SELECT id, university_email, role FROM users ORDER BY id;
SELECT id, seller_id, title, status FROM listings ORDER BY id;

-- 3. Insert a completed order for a specific listing/buyer pair.
--    Replace the three placeholder values, then run this INSERT.
--    (buyer_id must be the BUYER's user id, not the seller's.)
INSERT INTO orders (listing_id, buyer_id, total_amount, status, payment_method)
VALUES (
    1,              -- REPLACE with the listing's id from step 2
    2,              -- REPLACE with the buyer's user id from step 2
    2500.00,        -- REPLACE with the listing's price
    'COMPLETED',
    'CARD'
);

-- 4. Confirm the new order id, then set Postman's `completed_order_id`
--    environment variable to this value.
SELECT id, listing_id, buyer_id, total_amount, status FROM orders ORDER BY id DESC LIMIT 1;
