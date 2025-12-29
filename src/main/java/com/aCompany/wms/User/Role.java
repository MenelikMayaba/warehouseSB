package com.aCompany.wms.User;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum Role {
    RECEIVER(0),
    PICKER(1),
    PACKER(2),
    DISPATCHER(3),
    ADMIN(4),
    AUDITOR(5);

    private final int value;

    Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static Role fromValue(int value) {
        for (Role role : values()) {
            if (role.value == value) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown role value: " + value);
    }

    @Converter(autoApply = true)
    public static class RoleConverter implements AttributeConverter<Role, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Role role) {
            return role != null ? role.getValue() : null;
        }

        @Override
        public Role convertToEntityAttribute(Integer dbData) {
            return dbData != null ? Role.fromValue(dbData) : null;
        }
    }
}
