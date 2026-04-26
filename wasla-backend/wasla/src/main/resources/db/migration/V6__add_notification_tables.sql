-- ═══════════════════════════════════════════════════════════════════
-- V6: Add notification-related tables (device_tokens, notification_outbox)
-- ═══════════════════════════════════════════════════════════════════

-- ─── Device Tokens Table ─────────────────────────────────────────
-- Stores FCM device tokens for push notifications

CREATE TABLE IF NOT EXISTS device_tokens (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID NOT NULL,
    token           VARCHAR(512) NOT NULL UNIQUE,
    device_type     VARCHAR(20) NOT NULL CHECK (device_type IN ('ANDROID', 'IOS', 'WEB')),
    is_active       BOOLEAN NOT NULL DEFAULT TRUE,
    last_used_at    TIMESTAMP,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100)
);

-- Indexes for device_tokens
CREATE INDEX IF NOT EXISTS idx_device_token_user_id ON device_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_device_token_token ON device_tokens(token);

-- ─── Notification Outbox Table ───────────────────────────────────
-- Implements the outbox pattern for reliable notification delivery

CREATE TABLE IF NOT EXISTS notification_outbox (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_type          VARCHAR(60) NOT NULL,
    recipient_user_id   UUID NOT NULL,
    recipient_role      VARCHAR(20) NOT NULL,
    title               VARCHAR(255) NOT NULL,
    body                TEXT NOT NULL,
    data_payload        JSONB,
    job_id              UUID,
    bid_id              UUID,
    actor_user_id       UUID,
    priority            VARCHAR(10) NOT NULL,
    ttl_seconds         INTEGER NOT NULL,
    data_only           BOOLEAN NOT NULL DEFAULT FALSE,
    status              VARCHAR(20) NOT NULL,
    retry_count         INTEGER NOT NULL DEFAULT 0,
    max_retries         INTEGER NOT NULL,
    next_retry_at       TIMESTAMP NOT NULL,
    last_error          TEXT,
    fcm_message_id      VARCHAR(255),
    processed_at        TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP DEFAULT NOW(),
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

-- Indexes for notification_outbox
CREATE INDEX IF NOT EXISTS idx_notification_outbox_status ON notification_outbox(status);
CREATE INDEX IF NOT EXISTS idx_notification_outbox_next_retry ON notification_outbox(next_retry_at);
CREATE INDEX IF NOT EXISTS idx_notification_outbox_recipient ON notification_outbox(recipient_user_id);

-- ═══════════════════════════════════════════════════════════════════
-- Migration complete!
-- ═══════════════════════════════════════════════════════════════════
