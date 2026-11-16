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
 * TODO(Week 1 schema freeze): reservation (OneToOne), diningTable, customer, status (OrderStatus), totalAmount (DECIMAL(12,2)), paymentMethod (CASH / BANK_TRANSFER), paidAt, createdAt, items (OneToMany OrderItem)
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}