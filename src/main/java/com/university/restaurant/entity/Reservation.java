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
 * [M2] [Shared JPA Entity - Locked Week 1]
 * TODO(Week 1 schema freeze): customer (ManyToOne User), reservationDateTime, guestCount (1-12), specialNotes, status (ReservationStatus), diningTable (ManyToOne, nullable until confirmed), rejectReason, createdAt
 */
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}