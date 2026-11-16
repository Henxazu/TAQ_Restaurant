package com.university.restaurant.common.enums;

/**
 * [Shared - Locked Week 1]
 * PENDING -> COOKING -> SERVED, or PENDING -> CANCELLED (customer may cancel only while PENDING).
 */
public enum OrderItemStatus {
    PENDING,
    COOKING,
    SERVED,
    CANCELLED
}