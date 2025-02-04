package com.library.bible.rent.model;

public enum RentStatus {
    REQUESTED("대여신청"),
    CANCLED("대여취소"),
    IN_USE("대여중"),
    RETURNED("반납");

    private final String description;

    RentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
