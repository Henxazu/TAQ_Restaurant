package com.university.restaurant.controller;

import com.university.restaurant.common.response.ApiResponse;
import com.university.restaurant.dto.request.CategoryRequest;
import com.university.restaurant.dto.request.MenuItemRequest;
import com.university.restaurant.dto.response.CategoryDto;
import com.university.restaurant.dto.response.MenuItemDto;
import com.university.restaurant.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * [M1] EPIC-02 Menu &amp; Catalog (US-CUST-01, US-ADMIN-01).
 * <ul>
 *   <li>/api/v1/menu/** - public (GET only)</li>
 *   <li>/api/v1/admin/menu/** - Admin category &amp; dish CRUD (soft delete)</li>
 *   <li>PATCH /api/v1/staff/menu/{id}/stock - Staff/Admin quick stock toggle</li>
 * </ul>
 * Role checks are enforced by URL rules in {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    // =========================================================================
    // 1. Public Menu (US-CUST-01)
    // =========================================================================

    @GetMapping("/menu/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getActiveCategories() {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getActiveCategories()));
    }

    /** GET /api/v1/menu?categoryId=&keyword=&availableOnly= */
    @GetMapping("/menu")
    public ResponseEntity<ApiResponse<List<MenuItemDto>>> getPublicMenu(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        List<MenuItemDto> items = menuService.getPublicMenu(categoryId, keyword, availableOnly);
        String message = items.isEmpty() ? "No dishes found matching your criteria" : "OK";
        return ResponseEntity.ok(ApiResponse.ok(message, items));
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<ApiResponse<MenuItemDto>> getMenuItem(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getMenuItemById(id)));
    }

    // =========================================================================
    // 2. Admin Category CRUD (US-ADMIN-01)
    // =========================================================================

    @GetMapping("/admin/menu/categories")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategories() {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getAllCategoriesForAdmin()));
    }

    @PostMapping("/admin/menu/categories")
    public ResponseEntity<ApiResponse<CategoryDto>> createCategory(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Category created successfully", menuService.createCategory(request)));
    }

    @PutMapping("/admin/menu/categories/{id}")
    public ResponseEntity<ApiResponse<CategoryDto>> updateCategory(
            @PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Category updated successfully", menuService.updateCategory(id, request)));
    }

    /** Sets is_active from ?isActive= or body {"isActive": bool}; flips the state when omitted. */
    @PatchMapping("/admin/menu/categories/{id}/active")
    public ResponseEntity<ApiResponse<CategoryDto>> toggleCategoryActive(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean isActive,
            @RequestBody(required = false) Map<String, Boolean> body) {
        Boolean target = resolveFlag(isActive, body, "isActive");
        return ResponseEntity.ok(ApiResponse.ok("Category status updated", menuService.toggleCategoryActive(id, target)));
    }

    // =========================================================================
    // 3. Admin Menu Item CRUD & Soft Delete (US-ADMIN-01)
    // =========================================================================

    /** Admin list includes out-of-stock dishes; soft-deleted dishes are excluded. */
    @GetMapping("/admin/menu/items")
    public ResponseEntity<ApiResponse<List<MenuItemDto>>> getAllMenuItems(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(menuService.getAllMenuItemsForAdmin(categoryId, keyword)));
    }

    @PostMapping("/admin/menu/items")
    public ResponseEntity<ApiResponse<MenuItemDto>> createMenuItem(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Dish created successfully", menuService.createMenuItem(request)));
    }

    @PutMapping("/admin/menu/items/{id}")
    public ResponseEntity<ApiResponse<MenuItemDto>> updateMenuItem(
            @PathVariable Long id, @Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Dish updated successfully", menuService.updateMenuItem(id, request)));
    }

    /** Soft delete: keeps historical order items / invoices intact. */
    @DeleteMapping("/admin/menu/items/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMenuItem(@PathVariable Long id) {
        menuService.softDeleteMenuItem(id);
        return ResponseEntity.ok(ApiResponse.ok("Dish deleted successfully", null));
    }

    // =========================================================================
    // 4. Staff / Admin Quick Stock Toggle (US-ADMIN-01 Scenario 2)
    // =========================================================================

    /** Sets is_available from ?isAvailable= or body {"isAvailable": bool}; flips the state when omitted. */
    @PatchMapping("/staff/menu/{id}/stock")
    public ResponseEntity<ApiResponse<MenuItemDto>> updateStock(
            @PathVariable Long id,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestBody(required = false) Map<String, Boolean> body) {
        Boolean target = resolveFlag(isAvailable, body, "isAvailable");
        MenuItemDto item = menuService.updateMenuItemStock(id, target);
        String message = Boolean.TRUE.equals(item.getIsAvailable())
                ? "Dish marked as Available"
                : "Dish marked as Out of Stock";
        return ResponseEntity.ok(ApiResponse.ok(message, item));
    }

    private Boolean resolveFlag(Boolean param, Map<String, Boolean> body, String key) {
        if (param != null) {
            return param;
        }
        return body != null ? body.get(key) : null;
    }
}