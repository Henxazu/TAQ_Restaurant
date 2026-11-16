package com.university.restaurant.common.enums;

/**
 * [Shared - Locked Week 1]
 * RES_PENDING -> RES_CONFIRMED | RES_REJECTED | RES_CANCELLED; RES_CONFIRMED -> RES_CHECKED_IN | RES_NO_SHOW; RES_CHECKED_IN -> COMPLETED (on PAID).
 */
public enum ReservationStatus {
    RES_PENDING,
    RES_CONFIRMED,
    RES_REJECTED,
    RES_CANCELLED,
    RES_CHECKED_IN,
    RES_NO_SHOW,
    COMPLETED
}