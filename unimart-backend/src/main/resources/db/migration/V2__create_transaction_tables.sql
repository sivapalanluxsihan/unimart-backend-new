-- V2__create_transaction_tables.sql
-- Remaining entities from the Guide 01 entity catalogue: listing photos,
-- messaging, orders/payments, reviews and notifications. V1 must never be
-- edited after it has run on a shared environment; new tables always arrive
-- as a new versioned migration (see Guide 05, section 4).

CREATE TABLE listing_images (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    listing_id  BIGINT       NOT NULL,
    image_url   VARCHAR(500) NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    CONSTRAINT fk_listing_image_listing FOREIGN KEY (listing_id)
        REFERENCES listings(id) ON DELETE CASCADE,
    INDEX idx_listing_image_listing (listing_id, sort_order)
);

CREATE TABLE conversations (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    listing_id  BIGINT    NOT NULL,
    buyer_id    BIGINT    NOT NULL,
    seller_id   BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_conversation_listing FOREIGN KEY (listing_id) REFERENCES listings(id),
    CONSTRAINT fk_conversation_buyer   FOREIGN KEY (buyer_id)   REFERENCES users(id),
    CONSTRAINT fk_conversation_seller  FOREIGN KEY (seller_id)  REFERENCES users(id),
    CONSTRAINT uq_conversation_participants UNIQUE (listing_id, buyer_id, seller_id)
);

CREATE TABLE messages (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id  BIGINT    NOT NULL,
    sender_id        BIGINT    NOT NULL,
    message_text     TEXT      NOT NULL,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at          TIMESTAMP NULL,
    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id),
    CONSTRAINT fk_message_sender       FOREIGN KEY (sender_id)       REFERENCES users(id),
    INDEX idx_message_conversation (conversation_id, created_at)
);

CREATE TABLE orders (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    listing_id      BIGINT        NOT NULL,
    buyer_id        BIGINT        NOT NULL,
    total_amount    DECIMAL(12,2) NOT NULL,
    status          VARCHAR(30)   NOT NULL,
    payment_method  VARCHAR(30)   NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                   ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_listing FOREIGN KEY (listing_id) REFERENCES listings(id),
    CONSTRAINT fk_order_buyer   FOREIGN KEY (buyer_id)   REFERENCES users(id),
    CONSTRAINT chk_order_amount CHECK (total_amount >= 0),
    INDEX idx_order_buyer_history (buyer_id, created_at)
);

CREATE TABLE payments (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id            BIGINT        NOT NULL UNIQUE,
    amount              DECIMAL(12,2) NOT NULL,
    status              VARCHAR(30)   NOT NULL,
    provider_reference  VARCHAR(190)  NOT NULL,
    idempotency_key     VARCHAR(190)  NOT NULL,
    paid_at             TIMESTAMP     NULL,
    CONSTRAINT fk_payment_order         FOREIGN KEY (order_id) REFERENCES orders(id),
    CONSTRAINT uq_payment_provider_ref  UNIQUE (provider_reference),
    CONSTRAINT uq_payment_idempotency   UNIQUE (idempotency_key),
    CONSTRAINT chk_payment_amount       CHECK (amount >= 0)
);

CREATE TABLE reviews (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT      NOT NULL UNIQUE,
    reviewer_id  BIGINT      NOT NULL,
    reviewee_id  BIGINT      NOT NULL,
    rating       TINYINT     NOT NULL,
    comment      TEXT        NULL,
    created_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP
                              ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_review_order    FOREIGN KEY (order_id)    REFERENCES orders(id),
    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    CONSTRAINT fk_review_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
    CONSTRAINT chk_review_rating  CHECK (rating BETWEEN 1 AND 5),
    INDEX idx_review_reviewee (reviewee_id, created_at)
);

CREATE TABLE notifications (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    type        VARCHAR(60)  NOT NULL,
    title       VARCHAR(160) NOT NULL,
    body        TEXT         NOT NULL,
    is_read     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_notification_user_unread (user_id, is_read, created_at)
);
