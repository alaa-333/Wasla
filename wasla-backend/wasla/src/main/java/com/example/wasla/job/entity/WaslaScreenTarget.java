package com.example.wasla.job.entity;

/**
 * Screen target constants for deep-link navigation in push notifications.
 * Used in the notification data payload: Map.of("screen", WaslaScreenTarget.JOB_DETAIL)
 */
public final class WaslaScreenTarget {

    private WaslaScreenTarget() {}

    public static final String JOB_DETAIL     = "JOB_DETAIL";
    public static final String AVAILABLE_JOBS  = "AVAILABLE_JOBS";
    public static final String JOB_BIDS        = "JOB_BIDS";
}
