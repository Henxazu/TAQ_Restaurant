# GEMINI.md — Project Context & AI Coding Guidelines

> **Purpose:** Root Context File for **TAQ_Restaurant** (`com.university.restaurant`). Every developer (**M1**, **M2**, **M3**) and AI assistant must follow the architecture, domain invariants, API contracts, and module ownership defined here and in `spec/general/`.

## 1. Project Identity & Reference Documents

- **System:** Basic Restaurant Management Backend (`TAQ_Restaurant`) — Spring Boot REST API consumed by a decoupled Frontend.
- **Team Size & Timeline:** 3 Students (`M1`, `M2`, `M3`) | 4 Weeks
- **Specification Documents (`spec/general/`):**
  - `Restaurant Management 4week Sprint Plan And Package Structure.md`: Team role assignment (`M1`, `M2`, `M3`), 4-week sprint breakdown, and Spring Boot REST (JAR, no JSP) package hierarchy.
  - `Restaurant Management System Prd Specification.md`: Full PRD, RBAC Matrix, Mermaid User Flows & State Machine, and Gherkin Acceptance Criteria (`EPIC-01` to `EPIC-06`).
  - `Restaurant Ui Ux Design System Figma Spec.md`: "Warm & Modern Dining" UI/UX Design System tokens for the Frontend (`#C84B31` Terracotta, `#FAF7F2` Warm Ivory, `Playfair Display` + `Plus Jakarta Sans`).

## 2. Technology Stack & Strict Constraints

| Layer | Technology / Library | Mandatory Rules & Gotchas |
| :--- | :--- | :--- |
| **Language & Build** | Java 17+ / Apache Maven (`packaging: jar`) | Executable JAR on **Embedded Tomcat**. No `ServletInitializer`, no WAR, no `src/main/webapp`. |
| **Backend Framework** | Spring Boot 3.2+ (`web`, `data-jpa`, `security`, `validation`, `devtools`, springdoc) | **CRITICAL:** Spring Boot 3 uses **`jakarta.*`** (`jakarta.persistence.*`, `jakarta.servlet.*`, `jakarta.validation.*`). **NEVER** import `javax.persistence.*` or `javax.servlet.*`. |
| **API Layer** | `@RestController` under `/api/v1/**` | **No JSP/JSTL/Thymeleaf.** Every endpoint returns `ApiResponse<T>` (`{ success, message, data }`). Errors go through `BusinessException` → `GlobalExceptionHandler` (JSON 400/401/403/404/409). |
| **Security** | Spring Security + BCrypt + CORS | URL rules in `SecurityConfig`: `/api/v1/auth/**` & `GET /api/v1/menu/**` public; `/api/v1/customer/**` → `CUSTOMER`; `/api/v1/staff/**` → `STAFF`/`ADMIN`; `/api/v1/admin/**` → `ADMIN`. Customers may only access their own reservations/orders. Frontend origins configured in `app.cors.allowed-origins`. |
| **Database** | MySQL 8.0 (InnoDB, `@Transactional`) | Use `BigDecimal` in Java and `DECIMAL(12,2)` in MySQL for all prices/totals. Never hard-delete menu items referenced by orders (Soft Delete). `Order` entity maps to table `orders` (reserved word). |
| **Real-Time Updates** | Frontend 10s AJAX `GET` Polling + Manual Refresh | `GET /api/v1/customer/orders/active` (M2) and `GET /api/v1/staff/dashboard` (M3) share the `OrderPollDto` JSON contract (frozen end of Week 2). |

## 3. Package Structure & 3-Member Ownership

```text
src/main/java/com/university/restaurant/
├── RestaurantApplication.java
├── config/                        # [M1] SecurityConfig, CorsConfig, OpenApiConfig (Swagger, optional)
├── common/                        # [Shared - Locked Week 1]
│   ├── enums/                     # RoleType, TableStatus, ReservationStatus, OrderStatus, OrderItemStatus
│   ├── response/                  # ApiResponse<T> { success, message, data }
│   └── exception/                 # BusinessException, GlobalExceptionHandler (@RestControllerAdvice)
├── entity/                        # [Shared JPA Entities - Locked Week 1]
│   ├── User, Category, MenuItem       # [M1]
│   ├── Reservation                    # [M2]
│   ├── Order, OrderItem               # [M2 / M3]
│   └── DiningTable                    # [M3]
├── dto/
│   ├── request/                   # [M1/M2/M3] LoginRequest, BookingRequest, AddOrderItemsRequest...
│   └── response/                  # [M1/M2/M3] MenuItemDto, OrderPollDto, RevenueSummaryDto...
├── repository/                    # [M1/M2/M3] One Spring Data JPA Repository per Entity
├── service/
│   ├── AuthService, MenuService                          # [M1]
│   ├── ReservationService, CustomerOrderService          # [M2]
│   └── StaffOpsService, TableAdminService, ReportService # [M3]
└── controller/                    # All @RestController (/api/v1/**)
    ├── AuthController             # [M1] /api/v1/auth/**
    ├── MenuController             # [M1] /api/v1/menu/**, /api/v1/admin/menu/**, PATCH /api/v1/staff/menu/{id}/stock
    ├── ReservationController      # [M2] /api/v1/customer/reservations/**
    ├── CustomerOrderController    # [M2] /api/v1/customer/orders/**
    ├── StaffOpsController         # [M3] /api/v1/staff/**
    └── AdminPortalController      # [M3] /api/v1/admin/tables/**, /admin/staff/**, /admin/reports/**
```

### Resources & Tests
- `src/main/resources/application.properties` — MySQL datasource, JPA, CORS origins, springdoc, `server.port=8080`.
- `src/main/resources/data.sql` — Seed: 1 Admin, 2 Staff, 10 Dining Tables, 15 Menu Items.
- `src/main/resources/static/` — only if serving static HTML/CSS/JS directly from Spring Boot (otherwise the Frontend is a separate project).
- `src/test/java/com/university/restaurant/` — JUnit 5 + MockMvc (RBAC `403`, soft delete, transactional checkout).

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
  - `OrderStatus`: `ACTIVE` -> `PAYMENT_REQUESTED` (requires at least 1 `SERVED` item and no `PENDING`/`COOKING` items remaining; locks further add-on ordering) -> `PAID` (`@Transactional`, idempotent: sets `paid_at`, marks Reservation `COMPLETED`, releases Table to `AVAILABLE`). `CANCELLED` when the linked reservation is rejected/cancelled/no-show.
