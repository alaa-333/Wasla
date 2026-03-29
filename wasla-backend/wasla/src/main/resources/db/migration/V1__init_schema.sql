-- =========================================
-- V1: Initial schema for Wasla backend
-- Aligned with MoveMate Architecture docs
-- =========================================

-- Users table (authentication + identity)
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    full_name       VARCHAR(100)    NOT NULL,
    email           VARCHAR(100)    NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    phone           VARCHAR(20)     NOT NULL,
    fcm_token       VARCHAR(255),
    role            VARCHAR(20)     NOT NULL,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- Driver profiles (one-to-one with users where role=DRIVER)
CREATE TABLE IF NOT EXISTS driver_profiles (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL UNIQUE REFERENCES users(id),
    vehicle_type    VARCHAR(30)     NOT NULL,
    license_plate   VARCHAR(20)     NOT NULL UNIQUE,
    photo_url       VARCHAR(255),
    is_available    BOOLEAN         NOT NULL DEFAULT FALSE,
    current_lat     DOUBLE PRECISION,
    current_lng     DOUBLE PRECISION,
    rating_avg      DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    total_jobs      INTEGER         NOT NULL DEFAULT 0,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- Refresh tokens (for JWT token rotation)
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL,
    user_role       VARCHAR(20)     NOT NULL,
    token           VARCHAR(512)    NOT NULL UNIQUE,
    expires_at      TIMESTAMP       NOT NULL,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- Jobs (move requests from clients)
CREATE TABLE IF NOT EXISTS jobs (
    id              BIGSERIAL       PRIMARY KEY,
    client_id       BIGINT          NOT NULL REFERENCES users(id),
    driver_id       BIGINT          REFERENCES users(id),
    pickup_address  VARCHAR(255)    NOT NULL,
    pickup_lat      DOUBLE PRECISION NOT NULL,
    pickup_lng      DOUBLE PRECISION NOT NULL,
    dropoff_address VARCHAR(255)    NOT NULL,
    dropoff_lat     DOUBLE PRECISION NOT NULL,
    dropoff_lng     DOUBLE PRECISION NOT NULL,
    cargo_desc      TEXT,
    cargo_photo_url VARCHAR(255),
    status          VARCHAR(20)     NOT NULL DEFAULT 'OPEN',
    accepted_price  NUMERIC(10,2),
    expires_at      TIMESTAMP       NOT NULL,
    completed_at    TIMESTAMP,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- Bids (driver price quotes on jobs)
CREATE TABLE IF NOT EXISTS bids (
    id              BIGSERIAL       PRIMARY KEY,
    job_id          BIGINT          NOT NULL REFERENCES jobs(id),
    driver_id       BIGINT          NOT NULL REFERENCES users(id),
    price           NUMERIC(10,2)   NOT NULL,
    note            TEXT,
    status          VARCHAR(20)     NOT NULL DEFAULT 'PENDING',
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100),
    CONSTRAINT uk_bid_job_driver UNIQUE (job_id, driver_id)
);

-- Ratings (client rates driver after completed job)
CREATE TABLE IF NOT EXISTS ratings (
    id              BIGSERIAL       PRIMARY KEY,
    job_id          BIGINT          NOT NULL UNIQUE REFERENCES jobs(id),
    client_id       BIGINT          NOT NULL REFERENCES users(id),
    driver_id       BIGINT          NOT NULL REFERENCES users(id),
    score           INTEGER         NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment         VARCHAR(1000),
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP,
    created_by      VARCHAR(100),
    last_modified_by VARCHAR(100)
);

-- ========== Indexes ==========

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_role ON users(role);
CREATE INDEX IF NOT EXISTS idx_driver_profiles_user_id ON driver_profiles(user_id);
CREATE INDEX IF NOT EXISTS idx_driver_profiles_available ON driver_profiles(is_available);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_jobs_status ON jobs(status);
CREATE INDEX IF NOT EXISTS idx_jobs_client_id ON jobs(client_id);
CREATE INDEX IF NOT EXISTS idx_jobs_driver_id ON jobs(driver_id);
CREATE INDEX IF NOT EXISTS idx_jobs_expires_at ON jobs(expires_at);
CREATE INDEX IF NOT EXISTS idx_bids_job_id ON bids(job_id);
CREATE INDEX IF NOT EXISTS idx_bids_driver_id ON bids(driver_id);
CREATE INDEX IF NOT EXISTS idx_ratings_job_id ON ratings(job_id);
CREATE INDEX IF NOT EXISTS idx_ratings_driver_id ON ratings(driver_id);
