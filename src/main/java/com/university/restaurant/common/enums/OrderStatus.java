package com.university.restaurant.common.enums;

/**
 * [Shared - Locked Week 1]
 * ACTIVE -> PAYMENT_REQUESTED (>=1 SERVED, no PENDING/COOKING; locks add-ons) -> PAID (@Transactional: paid_at, Reservation COMPLETED, Table AVAILABLE).
 */
public enum OrderStatus {
    ACTIVE,
    PAYMENT_REQUESTED,
    PAID,
    CANCELLED
}