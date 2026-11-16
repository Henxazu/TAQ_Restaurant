package com.university.restaurant.repository;

import com.university.restaurant.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M2 / M3] */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}