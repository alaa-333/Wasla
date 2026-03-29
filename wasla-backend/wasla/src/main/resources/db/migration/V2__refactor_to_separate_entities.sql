-- ═══════════════════════════════════════════════════════════════════
-- V2: Refactor to separate Client and Driver entities with UUID
-- ═══════════════════════════════════════════════════════════════════

-- Enable UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ─── Step 1: Create new clients table ───────────────────────────

CREATE TABLE clients_new (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    fcm_token       VARCHAR(255),
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ─── Step 2: Create new drivers table ───────────────────────────

CREATE TABLE drivers_new (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    full_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(100) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    vehicle_type    VARCHAR(30)  NOT NULL,
    license_plate   VARCHAR(20)  NOT NULL UNIQUE,
    photo_url       VARCHAR(255),
    is_available    BOOLEAN      DEFAULT FALSE,
    current_lat     DECIMAL(10,7),
    current_lng     DECIMAL(10,7),
    rating_avg      DECIMAL(3,2) DEFAULT 0.00,
    total_jobs      INTEGER      DEFAULT 0,
    fcm_token       VARCHAR(255),
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ─── Step 3: Migrate data from users to clients ─────────────────

INSERT INTO clients_new (id, full_name, email, password, phone, fcm_token, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    full_name,
    email,
    password,
    phone,
    fcm_token,
    created_at,
    COALESCE(updated_at, created_at)
FROM users
WHERE role = 'CLIENT' AND is_deleted = false;

-- ─── Step 4: Migrate data from users + driver_profiles to drivers ───

INSERT INTO drivers_new (id, full_name, email, password, phone, vehicle_type, license_plate, 
                         photo_url, is_available, current_lat, current_lng, rating_avg, 
                         total_jobs, fcm_token, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    u.full_name,
    u.email,
    u.password,
    u.phone,
    dp.vehicle_type::VARCHAR,
    dp.license_plate,
    dp.photo_url,
    dp.is_available,
    dp.current_lat,
    dp.current_lng,
    dp.rating_avg,
    dp.total_jobs,
    u.fcm_token,
    u.created_at,
    COALESCE(u.updated_at, u.created_at)
FROM users u
INNER JOIN driver_profiles dp ON u.id = dp.user_id
WHERE u.role = 'DRIVER' AND u.is_deleted = false AND dp.is_deleted = false;

-- ─── Step 5: Create mapping tables for ID conversion ────────────

CREATE TEMP TABLE client_id_mapping AS
SELECT 
    u.id as old_id,
    c.id as new_id
FROM users u
INNER JOIN clients_new c ON u.email = c.email
WHERE u.role = 'CLIENT';

CREATE TEMP TABLE driver_id_mapping AS
SELECT 
    u.id as old_id,
    d.id as new_id
FROM users u
INNER JOIN drivers_new d ON u.email = d.email
WHERE u.role = 'DRIVER';

-- ─── Step 6: Create new jobs table with UUID ────────────────────

CREATE TABLE jobs_new (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    client_id       UUID NOT NULL,
    driver_id       UUID,
    pickup_address  VARCHAR(255) NOT NULL,
    pickup_lat      DECIMAL(10,7) NOT NULL,
    pickup_lng      DECIMAL(10,7) NOT NULL,
    dropoff_address VARCHAR(255) NOT NULL,
    dropoff_lat     DECIMAL(10,7) NOT NULL,
    dropoff_lng     DECIMAL(10,7) NOT NULL,
    cargo_desc      TEXT,
    cargo_photo_url VARCHAR(255),
    status          VARCHAR(20)  DEFAULT 'OPEN' NOT NULL,
    accepted_price  DECIMAL(10,2),
    expires_at      TIMESTAMP    NOT NULL,
    completed_at    TIMESTAMP,
    created_at      TIMESTAMP    DEFAULT NOW(),
    updated_at      TIMESTAMP    DEFAULT NOW()
);

-- ─── Step 7: Migrate jobs data ──────────────────────────────────

INSERT INTO jobs_new (id, client_id, driver_id, pickup_address, pickup_lat, pickup_lng,
                      dropoff_address, dropoff_lat, dropoff_lng, cargo_desc, cargo_photo_url,
                      status, accepted_price, expires_at, completed_at, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    cm.new_id,
    dm.new_id,
    j.pickup_address,
    j.pickup_lat,
    j.pickup_lng,
    j.dropoff_address,
    j.dropoff_lat,
    j.dropoff_lng,
    j.cargo_desc,
    j.cargo_photo_url,
    j.status,
    j.accepted_price,
    j.expires_at,
    j.completed_at,
    j.created_at,
    COALESCE(j.updated_at, j.created_at)
FROM jobs j
INNER JOIN client_id_mapping cm ON j.client_id = cm.old_id
LEFT JOIN driver_id_mapping dm ON j.driver_id = dm.old_id
WHERE j.is_deleted = false;

-- ─── Step 8: Create job ID mapping ──────────────────────────────

CREATE TEMP TABLE job_id_mapping AS
SELECT 
    j_old.id as old_id,
    j_new.id as new_id
FROM jobs j_old
INNER JOIN jobs_new j_new ON 
    j_old.pickup_address = j_new.pickup_address AND
    j_old.created_at = j_new.created_at;

-- ─── Step 9: Create new bids table ──────────────────────────────

CREATE TABLE bids_new (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id      UUID NOT NULL,
    driver_id   UUID NOT NULL,
    price       DECIMAL(10,2) NOT NULL,
    note        TEXT,
    status      VARCHAR(20) DEFAULT 'PENDING' NOT NULL,
    created_at  TIMESTAMP   DEFAULT NOW(),
    updated_at  TIMESTAMP   DEFAULT NOW(),
    UNIQUE (job_id, driver_id)
);

-- ─── Step 10: Migrate bids data ─────────────────────────────────

INSERT INTO bids_new (id, job_id, driver_id, price, note, status, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    jm.new_id,
    dm.new_id,
    b.price,
    b.note,
    b.status,
    b.created_at,
    COALESCE(b.updated_at, b.created_at)
FROM bids b
INNER JOIN job_id_mapping jm ON b.job_id = jm.old_id
INNER JOIN driver_id_mapping dm ON b.driver_id = dm.old_id
WHERE b.is_deleted = false;

-- ─── Step 11: Create new ratings table ──────────────────────────

CREATE TABLE ratings_new (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_id      UUID NOT NULL UNIQUE,
    client_id   UUID NOT NULL,
    driver_id   UUID NOT NULL,
    score       SMALLINT NOT NULL CHECK (score BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- ─── Step 12: Migrate ratings data ──────────────────────────────

INSERT INTO ratings_new (id, job_id, client_id, driver_id, score, comment, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    jm.new_id,
    cm.new_id,
    dm.new_id,
    r.score::SMALLINT,
    r.comment,
    r.created_at,
    COALESCE(r.updated_at, r.created_at)
FROM ratings r
INNER JOIN job_id_mapping jm ON r.job_id = jm.old_id
INNER JOIN client_id_mapping cm ON r.client_id = cm.old_id
INNER JOIN driver_id_mapping dm ON r.driver_id = dm.old_id
WHERE r.is_deleted = false;

-- ─── Step 13: Create new refresh_tokens table ───────────────────

CREATE TABLE refresh_tokens_new (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL,
    user_role   VARCHAR(20) NOT NULL,
    token       VARCHAR(512) UNIQUE NOT NULL,
    expires_at  TIMESTAMP NOT NULL,
    created_at  TIMESTAMP DEFAULT NOW(),
    updated_at  TIMESTAMP DEFAULT NOW()
);

-- ─── Step 14: Migrate refresh tokens ────────────────────────────

INSERT INTO refresh_tokens_new (id, user_id, user_role, token, expires_at, created_at, updated_at)
SELECT 
    gen_random_uuid(),
    COALESCE(cm.new_id, dm.new_id),
    rt.user_role,
    rt.token,
    rt.expires_at,
    rt.created_at,
    COALESCE(rt.updated_at, rt.created_at)
FROM refresh_tokens rt
LEFT JOIN client_id_mapping cm ON rt.user_id = cm.old_id AND rt.user_role = 'CLIENT'
LEFT JOIN driver_id_mapping dm ON rt.user_id = dm.old_id AND rt.user_role = 'DRIVER'
WHERE rt.is_deleted = false AND (cm.new_id IS NOT NULL OR dm.new_id IS NOT NULL);

-- ─── Step 15: Drop old tables ───────────────────────────────────

DROP TABLE IF EXISTS ratings CASCADE;
DROP TABLE IF EXISTS bids CASCADE;
DROP TABLE IF EXISTS jobs CASCADE;
DROP TABLE IF EXISTS refresh_tokens CASCADE;
DROP TABLE IF EXISTS driver_profiles CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- ─── Step 16: Rename new tables ─────────────────────────────────

ALTER TABLE clients_new RENAME TO clients;
ALTER TABLE drivers_new RENAME TO drivers;
ALTER TABLE jobs_new RENAME TO jobs;
ALTER TABLE bids_new RENAME TO bids;
ALTER TABLE ratings_new RENAME TO ratings;
ALTER TABLE refresh_tokens_new RENAME TO refresh_tokens;

-- ─── Step 17: Add foreign key constraints ───────────────────────

ALTER TABLE jobs
    ADD CONSTRAINT fk_jobs_client FOREIGN KEY (client_id) REFERENCES clients(id),
    ADD CONSTRAINT fk_jobs_driver FOREIGN KEY (driver_id) REFERENCES drivers(id);

ALTER TABLE bids
    ADD CONSTRAINT fk_bids_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    ADD CONSTRAINT fk_bids_driver FOREIGN KEY (driver_id) REFERENCES drivers(id);

ALTER TABLE ratings
    ADD CONSTRAINT fk_ratings_job FOREIGN KEY (job_id) REFERENCES jobs(id),
    ADD CONSTRAINT fk_ratings_client FOREIGN KEY (client_id) REFERENCES clients(id),
    ADD CONSTRAINT fk_ratings_driver FOREIGN KEY (driver_id) REFERENCES drivers(id);

-- ─── Step 18: Create indexes ────────────────────────────────────

CREATE INDEX idx_jobs_status ON jobs(status);
CREATE INDEX idx_jobs_client_id ON jobs(client_id);
CREATE INDEX idx_jobs_driver_id ON jobs(driver_id);
CREATE INDEX idx_jobs_expires_at ON jobs(expires_at);

CREATE INDEX idx_bids_job_id ON bids(job_id);
CREATE INDEX idx_bids_driver_id ON bids(driver_id);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);

-- ═══════════════════════════════════════════════════════════════════
-- Migration complete!
-- ═══════════════════════════════════════════════════════════════════
