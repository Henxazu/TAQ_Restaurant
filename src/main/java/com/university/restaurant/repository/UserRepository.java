package com.university.restaurant.repository;

import com.university.restaurant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** [M1] */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}