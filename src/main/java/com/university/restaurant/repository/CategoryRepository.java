package com.university.restaurant.repository;

import com.university.restaurant.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M1] */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
}