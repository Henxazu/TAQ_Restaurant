package com.university.restaurant.repository;

import com.university.restaurant.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M2 / M3] */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}