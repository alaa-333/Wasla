-- ═══════════════════════════════════════════════════════════════════
-- V3: Add location_history table for GPS tracking
-- ═══════════════════════════════════════════════════════════════════

CREATE TABLE location_history (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    driver_id       UUID NOT NULL REFERENCES drivers(id) ON DELETE CASCADE,
    job_id          UUID NOT NULL REFERENCES jobs(id) ON DELETE CASCADE,
    lat             DECIMAL(10, 7) NOT NULL,
    lng             DECIMAL(10, 7) NOT NULL,
    speed           DECIMAL(10, 2),
    accuracy        DECIMAL(10, 2),
    heading         DECIMAL(10, 2),
    timestamp       TIMESTAMP NOT NULL,
    created_at      TIMESTAMP DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW()
);

-- Indexes for performance
CREATE INDEX idx_location_history_driver_id ON location_history(driver_id);
CREATE INDEX idx_location_history_job_id ON location_history(job_id);
CREATE INDEX idx_location_history_timestamp ON location_history(timestamp DESC);
CREATE INDEX idx_location_history_job_timestamp ON location_history(job_id, timestamp DESC);

-- Comments
COMMENT ON TABLE location_history IS 'Stores GPS location updates from drivers during jobs';
COMMENT ON COLUMN location_history.lat IS 'Latitude coordinate';
COMMENT ON COLUMN location_history.lng IS 'Longitude coordinate';
COMMENT ON COLUMN location_history.speed IS 'Speed in km/h';
COMMENT ON COLUMN location_history.accuracy IS 'GPS accuracy in meters';
COMMENT ON COLUMN location_history.heading IS 'Direction in degrees (0-360)';

-- ═══════════════════════════════════════════════════════════════════
-- Migration complete!
-- ═══════════════════════════════════════════════════════════════════
