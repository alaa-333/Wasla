-- ═══════════════════════════════════════════════════════════════════
-- V5: Add auditing columns to all tables
-- ═══════════════════════════════════════════════════════════════════

-- Add auditing columns to clients table
ALTER TABLE clients ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE clients ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Add auditing columns to drivers table
ALTER TABLE drivers ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE drivers ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Add auditing columns to jobs table
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE jobs ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Add auditing columns to bids table
ALTER TABLE bids ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE bids ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Add auditing columns to ratings table
ALTER TABLE ratings ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE ratings ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- Add auditing columns to refresh_tokens table
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS created_by VARCHAR(100);
ALTER TABLE refresh_tokens ADD COLUMN IF NOT EXISTS updated_by VARCHAR(100);

-- ═══════════════════════════════════════════════════════════════════
-- Migration complete!
-- ═══════════════════════════════════════════════════════════════════
