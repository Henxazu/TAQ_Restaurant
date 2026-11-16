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
 * [M3] [Shared JPA Entity - Locked Week 1]
 * TODO(Week 1 schema freeze): tableCode (unique, e.g. T-01), capacity (>= 1), zone, status (TableStatus), @Version for assignment locking
 */
@Entity
@Table(name = "dining_tables")
@Getter
@Setter
@NoArgsConstructor
public class DiningTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}