package com.example.wasla.user.driver.entity;

import lombok.RequiredArgsConstructor;

/**
 * Vehicle types available for drivers.
 * Each type has a maximum cargo capacity in tons.
 */
@RequiredArgsConstructor
public enum VehicleType {
    PICKUP_HALF_TON(0.5),
    PICKUP_ONE_TON(1.0),
    PICKUP_TWO_TON(2.0),
    DABABA_3_TON(3.0),
    DABABA_5_TON(5.0),
    ISUZU_10_TON(10.0),
    TRAILER_20_TON(20.0);

    private final double maxCapacityTons;

    public double getMaxCapacityTons() {
        return maxCapacityTons;
    }
}
