-- V3__modify_reviews_rating_to_int.sql
-- Align reviews.rating column type with Review entity (Integer -> Types#INTEGER)
ALTER TABLE reviews MODIFY COLUMN rating INT NOT NULL;
