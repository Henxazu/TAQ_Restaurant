package com.university.restaurant.repository;

import com.university.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** [M1] */
@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    /**
     * Find a non-deleted menu item with its category eagerly fetched.
     */
    @Query("""
            SELECT m FROM MenuItem m
            JOIN FETCH m.category c
            WHERE m.id = :id AND m.isDeleted = false
            """)
    Optional<MenuItem> findActiveById(@Param("id") Long id);

    /**
     * Public menu search & filter:
     * - Excludes soft-deleted dishes (m.isDeleted = false)
     * - Only shows dishes under active categories (c.isActive = true) per US-CUST-01
     * - Optional category filter, keyword search (case-insensitive on name), and availableOnly flag
     */
    @Query("""
            SELECT m FROM MenuItem m
            JOIN FETCH m.category c
            WHERE m.isDeleted = false
              AND c.isActive = true
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:availableOnly = false OR m.isAvailable = true)
              AND (:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY c.id ASC, m.id ASC
            """)
    List<MenuItem> searchPublicMenu(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword,
            @Param("availableOnly") boolean availableOnly
    );

    /**
     * Admin menu catalog: returns all non-deleted items (including items in inactive categories),
     * optionally filtered by category or keyword.
     */
    @Query("""
            SELECT m FROM MenuItem m
            JOIN FETCH m.category c
            WHERE m.isDeleted = false
              AND (:categoryId IS NULL OR c.id = :categoryId)
              AND (:keyword IS NULL OR LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY c.id ASC, m.id ASC
            """)
    List<MenuItem> findAllForAdmin(
            @Param("categoryId") Long categoryId,
            @Param("keyword") String keyword
    );

    boolean existsByNameIgnoreCaseAndCategoryIdAndIsDeletedFalse(String name, Long categoryId);

    boolean existsByNameIgnoreCaseAndCategoryIdAndIsDeletedFalseAndIdNot(String name, Long categoryId, Long id);

    long countByCategoryIdAndIsDeletedFalse(Long categoryId);
}
