package com.university.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * [M2 / M3] [Shared JPA Entity - Locked Week 1]
 * TODO(Week 1 schema freeze): order (ManyToOne), menuItem (ManyToOne), quantity (1-20), unitPrice (price at order time, DECIMAL(12,2)), status (OrderItemStatus), createdAt
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}