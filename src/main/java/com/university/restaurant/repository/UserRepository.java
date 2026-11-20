package com.university.restaurant.repository;

import com.university.restaurant.common.enums.RoleType;
import com.university.restaurant.common.enums.UserStatus;
import com.university.restaurant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** [M1] */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String email, String phone);

    boolean existsByEmail(String email);

    boolean existsByPhone(String phone);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    List<User> findByRoleOrderByCreatedAtDesc(RoleType role);

    List<User> findByRoleAndStatusOrderByCreatedAtDesc(RoleType role, UserStatus status);
}
