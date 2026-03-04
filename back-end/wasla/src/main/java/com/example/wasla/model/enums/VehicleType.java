package com.example.wasla.model.enums;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VehicleType {
    PICKUP_HALF_TON(0.5),  // نص نقل صغير (0.5 طن)
    PICKUP_ONE_TON(1.0),   // نص نقل 1 طن
    PICKUP_TWO_TON(2.0),   // نص نقل 2 طن
    DABABA_3_TON(3.0),       // دبابة 3 طن
    DABABA_5_TON(5.0),      // دبابة 5 طن
    ISUZU_10_TON(10.0),    // إيسوزو 10 طن
    TRAILER_20_TON(20.0);  // تريلا 20 طن

    private final double maxCapacityTons;



    public double getMaxCapacityTons() {
        return maxCapacityTons;
    }
}