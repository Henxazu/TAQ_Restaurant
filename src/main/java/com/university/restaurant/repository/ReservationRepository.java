package com.university.restaurant.repository;

import com.university.restaurant.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M2] */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}