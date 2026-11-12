# GEMINI.md — Project Context & AI Coding Guidelines

> **Purpose:** Root Context File for **TAQ_Restaurant** (`com.university.restaurant`). Every developer (**M1**, **M2**, **M3**) and AI assistant must follow the architecture, domain invariants, UI/UX design tokens, and module ownership defined here and in `spec/general/`.

## 1. Project Identity & Reference Documents

- **System:** Basic Restaurant Management Web Application (`TAQ_Restaurant`)
- **Team Size & Timeline:** 3 Students (`M1`, `M2`, `M3`) | 4 Weeks
- **Specification Documents (`spec/general/`):**
  - `Restaurant Management 4week Sprint Plan And Package Structure.md`: Team role assignment (`M1`, `M2`, `M3`), 4-week sprint breakdown, and Spring Boot + JSP/JSTL package hierarchy.
  - `Restaurant Management System Prd Specification.md`: Full PRD, RBAC Matrix, Mermaid User Flows & State Machine, and Gherkin Acceptance Criteria (`EPIC-01` to `EPIC-06`).
  - `Restaurant Ui Ux Design System Figma Spec.md`: "Warm & Modern Dining" UI/UX Design System tokens, Color Palette (`#C84B31` Terracotta, `#FAF7F2` Warm Ivory), Typography (`Playfair Display` + `Plus Jakarta Sans`), Component specs, and Screen layouts.

## 2. Technology Stack & Strict Constraints

| Layer | Technology / Library | Mandatory Rules & Gotchas |
| :--- | :--- | :--- |
| **Language & Build** | Java 17+ / Apache Maven (`packaging: war`) | Must use `<packaging>war</packaging>` with `ServletInitializer extends SpringBootServletInitializer` for Apache Tomcat 10.x JSP compilation. |
| **Backend Framework** | Spring Boot 3.2+ (`spring-boot-starter-web`, `data-jpa`, `security`, `validation`) | **CRITICAL:** Spring Boot 3 uses **`jakarta.*`** namespace (`jakarta.persistence.*`, `jakarta.servlet.*`, `jakarta.validation.*`). **NEVER** import `javax.persistence.*` or `javax.servlet.*`. |
| **View Layer (SSR)** | JSP & JSTL + Bootstrap 5 | Place all `.jsp` views inside `src/main/webapp/WEB-INF/views/**`. Use Jakarta JSTL 3.0 URI: `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` and `<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>`. |
| **Database** | MySQL 8.0 (InnoDB, `@Transactional`) | Use `BigDecimal` in Java and `DECIMAL(12,2)` in MySQL for all prices/totals. Never hard-delete menu items referenced by orders (use Soft Delete). |
| **Real-Time Updates** | Lightweight 10s AJAX `GET` Polling + Manual Refresh | Use `customer-poll.js` (`/customer/active-order`) and `staff-poll.js` (`/staff/dashboard`) sharing `OrderStatusPollResponse.java`. |

## 3. Package Structure & 3-Member Ownership

```text
src/main/java/com/university/restaurant/
├── RestaurantApplication.java
├── ServletInitializer.java        # Tomcat WAR initializer
├── config/                        # [M1] SecurityConfig, WebMvcConfig
├── common/                        # [Shared - Locked Week 1]
│   ├── enums/                     # RoleType, TableStatus, ReservationStatus, OrderStatus, OrderItemStatus
│   └── exception/                 # GlobalExceptionHandler.java
├── entity/                        # [Shared Entities - Locked Week 1]
│   ├── User, Category, MenuItem       # [M1]
│   ├── Reservation, Order, OrderItem  # [M2 / M3]
│   └── DiningTable                    # [M3]
├── dto/
│   ├── cart/                      # [M2] CartItemDto, PreOrderCartSession
│   ├── polling/                   # [M2/M3] OrderStatusPollResponse (JSON for 10s AJAX)
│   └── report/                    # [M3] RevenueSummaryDto, TopDishDto
├── repository/                    # [M1/M2/M3] Spring Data JPA Repositories per Entity
├── service/
│   ├── AuthService, MenuService                          # [M1]
│   ├── ReservationService, CustomerOrderService          # [M2]
│   └── StaffOpsService, TableAdminService, ReportService # [M3]
└── controller/
    ├── AuthController, MenuController                    # [M1]
    ├── ReservationController, CustomerOrderController    # [M2]
    └── StaffDashboardController, AdminPortalController   # [M3]
```

### JSP View Hierarchy (`src/main/webapp/WEB-INF/views/`)
- `layout/` (**M1**): `header.jsp`, `navbar.jsp`, `footer.jsp`, `alerts.jsp`
- `auth/` (**M1**): `login.jsp`, `register.jsp`, `profile.jsp`
- `menu/` (**M1**): `public-menu.jsp`, `admin-menu-list.jsp`, `admin-menu-form.jsp`
- `customer/` (**M2**): `cart-drawer.jsp`, `reservation-form.jsp`, `my-reservations.jsp`, `active-order.jsp`
- `staff/` (**M3**): `dashboard.jsp`, `checkout-modal.jsp`, `invoice-print.jsp`
- `admin/` (**M3**): `table-list.jsp`, `staff-list.jsp`, `revenue-report.jsp`

## 4. Core Domain State Machines & Rules

- **Roles (`RoleType`):** `ROLE_CUSTOMER`, `ROLE_STAFF`, `ROLE_ADMIN`.
- **Reservation Lifecycle (`ReservationStatus`):**
  - `RES_PENDING` -> `RES_CONFIRMED` (Staff assigns available table with `capacity >= guestCount`) | `RES_REJECTED` | `RES_CANCELLED` (Customer cancels while `RES_PENDING`).
  - `RES_CONFIRMED` -> `RES_CHECKED_IN` (Staff checks in guest -> Table becomes `OCCUPIED` & pre-order items activate) | `RES_NO_SHOW`.
  - `RES_CHECKED_IN` -> `COMPLETED` (upon counter payment `PAID`).
- **Dining Table Lifecycle (`TableStatus`):**
  - `AVAILABLE`, `RESERVED`, `OCCUPIED`, `PAYMENT_REQUESTED`, `MAINTENANCE`.
- **Order & OrderItem Lifecycle (`OrderStatus`, `OrderItemStatus`):**
  - `OrderItemStatus`: `PENDING` -> `COOKING` -> `SERVED` (or `PENDING` -> `CANCELLED`). Customers can cancel an item **only** while its status is `PENDING`.
  - `OrderStatus`: Active dine-in order -> `PAYMENT_REQUESTED` (requires at least 1 `SERVED` item and no `PENDING`/`COOKING` items remaining; locks further add-on ordering) -> `PAID` (`@Transactional`: sets `paid_at`, marks Reservation `COMPLETED`, releases Table to `AVAILABLE`).

