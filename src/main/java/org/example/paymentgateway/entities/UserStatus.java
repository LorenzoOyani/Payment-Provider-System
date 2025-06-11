package org.example.paymentgateway.entities;

import lombok.Getter;

public enum UserStatus {
    ACTIVE(true),
    INACTIVE(false);


    private final boolean booleanValue;

    UserStatus(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }
    public static UserStatus fromBooleanValue(boolean booleanValue) {
        return booleanValue ? ACTIVE : INACTIVE;
    }
}
