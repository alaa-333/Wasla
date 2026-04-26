-- ═══════════════════════════════════════════════════════════════════
-- V4: Enable PostGIS and add spatial indexes for location queries
-- ═══════════════════════════════════════════════════════════════════

-- Enable PostGIS extension (required for spatial queries)
CREATE EXTENSION IF NOT EXISTS postgis;

-- Add spatial indexes for jobs table (pickup location)
-- Using geometry type (SRID 4326) for better compatibility
CREATE INDEX IF NOT EXISTS idx_jobs_pickup_location ON jobs
USING GIST (ST_SetSRID(ST_MakePoint(pickup_lng, pickup_lat), 4326));

-- Add spatial indexes for jobs table (dropoff location)
CREATE INDEX IF NOT EXISTS idx_jobs_dropoff_location ON jobs
USING GIST (ST_SetSRID(ST_MakePoint(dropoff_lng, dropoff_lat), 4326));

-- Add spatial index for drivers current location
CREATE INDEX IF NOT EXISTS idx_drivers_current_location ON drivers
USING GIST (ST_SetSRID(ST_MakePoint(current_lng, current_lat), 4326));

-- Add comments for documentation
COMMENT ON COLUMN jobs.pickup_lat IS 'Pickup latitude coordinate (WGS84)';
COMMENT ON COLUMN jobs.pickup_lng IS 'Pickup longitude coordinate (WGS84)';
COMMENT ON COLUMN jobs.dropoff_lat IS 'Dropoff latitude coordinate (WGS84)';
COMMENT ON COLUMN jobs.dropoff_lng IS 'Dropoff longitude coordinate (WGS84)';
COMMENT ON COLUMN drivers.current_lat IS 'Driver current latitude (WGS84)';
COMMENT ON COLUMN drivers.current_lng IS 'Driver current longitude (WGS84)';

-- ═══════════════════════════════════════════════════════════════════
-- Migration complete!
-- PostGIS spatial queries are now available
-- ═══════════════════════════════════════════════════════════════════