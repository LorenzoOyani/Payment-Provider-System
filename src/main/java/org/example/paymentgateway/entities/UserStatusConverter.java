package org.example.paymentgateway.entities;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.example.paymentgateway.enums.UserStatus;

@Converter(autoApply = true)
public class UserStatusConverter implements AttributeConverter<UserStatus, Boolean> {
    @Override
    public Boolean convertToDatabaseColumn(UserStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getBooleanValue();
    }

    @Override
    public UserStatus convertToEntityAttribute(Boolean dbData) {
        if (dbData == null) {
            return null;
        }
        return UserStatus.fromBooleanValue(dbData);
    }
}
