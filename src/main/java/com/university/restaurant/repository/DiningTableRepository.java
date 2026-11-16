package com.university.restaurant.repository;

import com.university.restaurant.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M3] */
@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
}