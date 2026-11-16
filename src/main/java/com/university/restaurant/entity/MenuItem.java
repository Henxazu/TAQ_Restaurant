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
 * [M1] [Shared JPA Entity - Locked Week 1]
 * TODO(Week 1 schema freeze): name (max 100), description, price (BigDecimal / DECIMAL(12,2) > 0), imageUrl, isAvailable, isDeleted (soft delete), category (ManyToOne)
 */
@Entity
@Table(name = "menu_items")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}