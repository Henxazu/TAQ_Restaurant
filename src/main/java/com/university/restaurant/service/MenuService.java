package com.university.restaurant.service;

import com.university.restaurant.common.exception.BusinessException;
import com.university.restaurant.dto.request.CategoryRequest;
import com.university.restaurant.dto.request.MenuItemRequest;
import com.university.restaurant.dto.response.CategoryDto;
import com.university.restaurant.dto.response.MenuItemDto;
import com.university.restaurant.entity.Category;
import com.university.restaurant.entity.MenuItem;
import com.university.restaurant.repository.CategoryRepository;
import com.university.restaurant.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * [M1] Public menu search/filter, Admin category and dish CRUD (soft delete),
 * and Staff/Admin quick stock toggle (EPIC-02: US-CUST-01, US-ADMIN-01).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final CategoryRepository categoryRepository;
    private final MenuItemRepository menuItemRepository;

    // =========================================================================
    // 1. Public Menu & Category Browsing (US-CUST-01)
    // =========================================================================

    /**
     * Returns only active categories (`is_active = true`) for the public menu.
     */
    public List<CategoryDto> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByIdAsc()
                .stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    /**
     * Searches and filters public menu items.
     * Only active categories and non-deleted dishes are returned.
     */
    public List<MenuItemDto> getPublicMenu(Long categoryId, String keyword, boolean availableOnly) {
        String sanitizedKeyword = sanitizeKeyword(keyword);
        return menuItemRepository.searchPublicMenu(categoryId, sanitizedKeyword, availableOnly)
                .stream()
                .map(MenuItemDto::fromEntity)
                .toList();
    }

    /**
     * Retrieves details of a single non-deleted dish by ID.
     */
    public MenuItemDto getMenuItemById(Long id) {
        MenuItem item = findActiveMenuItemEntity(id);
        return MenuItemDto.fromEntity(item);
    }

    // =========================================================================
    // 2. Admin Category CRUD (US-ADMIN-01)
    // =========================================================================

    public List<CategoryDto> getAllCategoriesForAdmin() {
        return categoryRepository.findAll()
                .stream()
                .map(CategoryDto::fromEntity)
                .toList();
    }

    @Transactional
    public CategoryDto createCategory(CategoryRequest request) {
        String trimmedName = request.getName().trim();
        if (categoryRepository.existsByNameIgnoreCase(trimmedName)) {
            throw BusinessException.conflict("Category name '" + trimmedName + "' already exists");
        }

        Category category = Category.builder()
                .name(trimmedName)
                .description(trimToNull(request.getDescription()))
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return CategoryDto.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryEntity(id);
        String trimmedName = request.getName().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(trimmedName, id)) {
            throw BusinessException.conflict("Category name '" + trimmedName + "' already exists");
        }

        category.setName(trimmedName);
        category.setDescription(trimToNull(request.getDescription()));
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }

        return CategoryDto.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryDto toggleCategoryActive(Long id, Boolean isActive) {
        Category category = findCategoryEntity(id);
        boolean nextState = (isActive != null) ? isActive : !Boolean.TRUE.equals(category.getIsActive());
        category.setIsActive(nextState);
        return CategoryDto.fromEntity(categoryRepository.save(category));
    }

    // =========================================================================
    // 3. Admin Menu Item CRUD & Soft Delete (US-ADMIN-01)
    // =========================================================================

    public List<MenuItemDto> getAllMenuItemsForAdmin(Long categoryId, String keyword) {
        String sanitizedKeyword = sanitizeKeyword(keyword);
        return menuItemRepository.findAllForAdmin(categoryId, sanitizedKeyword)
                .stream()
                .map(MenuItemDto::fromEntity)
                .toList();
    }

    @Transactional
    public MenuItemDto createMenuItem(MenuItemRequest request) {
        validatePrice(request.getPrice());
        Category category = findCategoryEntity(request.getCategoryId());
        String trimmedName = request.getName().trim();

        if (menuItemRepository.existsByNameIgnoreCaseAndCategoryIdAndIsDeletedFalse(trimmedName, category.getId())) {
            throw BusinessException
                    .conflict("Dish '" + trimmedName + "' already exists in category '" + category.getName() + "'");
        }

        MenuItem item = MenuItem.builder()
                .category(category)
                .name(trimmedName)
                .description(trimToNull(request.getDescription()))
                .price(request.getPrice())
                .imageUrl(trimToNull(request.getImageUrl()))
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .isDeleted(false)
                .build();

        return MenuItemDto.fromEntity(menuItemRepository.save(item));
    }

    @Transactional
    public MenuItemDto updateMenuItem(Long id, MenuItemRequest request) {
        validatePrice(request.getPrice());
        MenuItem item = findActiveMenuItemEntity(id);
        Category category = findCategoryEntity(request.getCategoryId());
        String trimmedName = request.getName().trim();

        if (menuItemRepository.existsByNameIgnoreCaseAndCategoryIdAndIsDeletedFalseAndIdNot(trimmedName,
                category.getId(), id)) {
            throw BusinessException
                    .conflict("Dish '" + trimmedName + "' already exists in category '" + category.getName() + "'");
        }

        item.setCategory(category);
        item.setName(trimmedName);
        item.setDescription(trimToNull(request.getDescription()));
        item.setPrice(request.getPrice());
        item.setImageUrl(trimToNull(request.getImageUrl()));
        if (request.getIsAvailable() != null) {
            item.setIsAvailable(request.getIsAvailable());
        }

        return MenuItemDto.fromEntity(menuItemRepository.save(item));
    }

    /**
     * Soft-deletes a menu item (`is_deleted = true`, `is_available = false`)
     * so historical order items and invoices referencing this dish remain intact.
     */
    @Transactional
    public void softDeleteMenuItem(Long id) {
        MenuItem item = findActiveMenuItemEntity(id);
        item.setIsDeleted(true);
        item.setIsAvailable(false);
        menuItemRepository.save(item);
    }

    // =========================================================================
    // 4. Staff / Admin Quick Stock Toggle (US-ADMIN-01 Scenario 2)
    // =========================================================================

    /**
     * Toggles or sets `is_available` (`Available` / `Out of Stock`) during service.
     * If `isAvailable` parameter is null, flips the current boolean state.
     */
    @Transactional
    public MenuItemDto updateMenuItemStock(Long id, Boolean isAvailable) {
        MenuItem item = findActiveMenuItemEntity(id);
        boolean nextAvailability = (isAvailable != null) ? isAvailable : !Boolean.TRUE.equals(item.getIsAvailable());
        item.setIsAvailable(nextAvailability);
        return MenuItemDto.fromEntity(menuItemRepository.save(item));
    }

    // =========================================================================
    // Internal Helpers
    // =========================================================================

    private Category findCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> BusinessException.notFound("Category not found with id: " + id));
    }

    private MenuItem findActiveMenuItemEntity(Long id) {
        return menuItemRepository.findActiveById(id)
                .orElseThrow(() -> BusinessException.notFound("Menu item not found with id: " + id));
    }

    private void validatePrice(BigDecimal price) {
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw BusinessException.badRequest("Dish price must be greater than 0");
        }
    }

    private String sanitizeKeyword(String keyword) {
        if (keyword == null) {
            return null;
        }
        String trimmed = keyword.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
