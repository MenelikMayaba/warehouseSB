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
    public static class RoleConverter implements AttributeConverter<Role, String> {
        @Override
        public String convertToDatabaseColumn(Role role) {
            return role != null ? role.name() : null;
        }

        @Override
        public Role convertToEntityAttribute(String dbData) {
            if (dbData == null) {
                return null;
            }
            try {
                // First try to parse as enum name (for new data)
                return Role.valueOf(dbData);
            } catch (IllegalArgumentException e) {
                try {
                    // If that fails, try to parse as integer (for existing data)
                    int value = Integer.parseInt(dbData);
                    return Role.fromValue(value);
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Unknown role value: " + dbData, ex);
                }
            }
        }
    }
}
