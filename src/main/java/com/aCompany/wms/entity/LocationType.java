package com.aCompany.wms.entity;

public enum LocationType {
    PICKING("PICKING"),
    STORAGE("STORAGE"),
    RECEIVING("RECEIVING"),
    SHIPPING("SHIPPING"),
    HIGH_FREQUENCY("HIGH_FREQUENCY"),
    COLD_STORAGE("COLD_STORAGE"),
    BULK_STORAGE("BULK_STORAGE");

    private final String value;

    LocationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}