package com.example.wasla.job.entity;

/**
 * Job lifecycle states as defined in the architecture docs.
 * OPEN → BIDDING → CONFIRMED → IN_PROGRESS → COMPLETED | EXPIRED
 */
public enum JobStatus {
    OPEN,
    BIDDING,
    CONFIRMED,
    IN_PROGRESS,
    COMPLETED,
    EXPIRED,
    CANCELLED
}
