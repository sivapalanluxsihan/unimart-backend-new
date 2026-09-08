-- V1__create_core_tables.sql
-- Core identity/catalog tables. See Guide 05, section 4.

CREATE TABLE users (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    university_email  VARCHAR(190) NOT NULL UNIQUE,
    password_hash     VARCHAR(100) NOT NULL,
    full_name         VARCHAR(120) NOT NULL,
    role              VARCHAR(30)  NOT NULL,
    email_verified    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                                    ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE categories (
    id     BIGINT PRIMARY KEY AUTO_INCREMENT,
    name   VARCHAR(80) NOT NULL UNIQUE,
    active BOOLEAN     NOT NULL DEFAULT TRUE
);

CREATE TABLE listings (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id    BIGINT       NOT NULL,
    category_id  BIGINT       NOT NULL,
    title        VARCHAR(160) NOT NULL,
    description  TEXT         NOT NULL,
    price        DECIMAL(12,2) NOT NULL,
    status       VARCHAR(30)  NOT NULL,
    version      BIGINT       NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
                               ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_listing_seller   FOREIGN KEY (seller_id)   REFERENCES users(id),
    CONSTRAINT fk_listing_category FOREIGN KEY (category_id) REFERENCES categories(id),
    CONSTRAINT chk_listing_price   CHECK (price >= 0),
    INDEX idx_listing_search (status, category_id, created_at)
);
