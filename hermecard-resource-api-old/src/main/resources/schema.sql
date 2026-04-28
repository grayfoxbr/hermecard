CREATE TABLE IF NOT EXISTS cards (
    id               BIGSERIAL PRIMARY KEY,
    owner_username   VARCHAR(255)   NOT NULL,
    name             VARCHAR(255)   NOT NULL,
    brand            VARCHAR(100)   NOT NULL,
    card_limit       NUMERIC(15, 2) NOT NULL,
    current_balance  NUMERIC(15, 2) NOT NULL DEFAULT 0,
    closing_day      INT            NOT NULL,
    due_day          INT            NOT NULL,
    active           BOOLEAN        NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
);
