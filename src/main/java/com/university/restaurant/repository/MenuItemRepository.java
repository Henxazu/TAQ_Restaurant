package com.university.restaurant.repository;

import com.university.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M1] */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}