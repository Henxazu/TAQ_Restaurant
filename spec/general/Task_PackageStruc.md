# 4-Week Sprint Plan & Spring Boot Backend Package Structure
**System:** Basic Restaurant Management Web Application  
**Course:** Web Application Development (University Capstone Project)  
**Team Size & Timeline:** 3 Students | 4 Weeks  
**Technology Stack:** Java 17+, Spring Boot 3.x (Spring Web REST API, Spring Data JPA, Spring Security), MySQL 8.0, Apache Maven (JAR), Embedded Tomcat  

## 1. Team Role Assignment & Module Ownership (3 Students)

To prevent Git merge conflicts and balance workload across the 4-week timeline, each team member owns distinct vertical slices (`Controller -> Service -> Repository -> Frontend Integration`) while sharing the core JPA Entities and `ApiResponse` contracts locked in Week 1:

| Member | Module Ownership | Mapped Epics & Stories | Core Deliverables |
| :--- | :--- | :--- | :--- |
| **Member 1 (M1)** | Core Foundation, Auth & Menu Catalog Lead | `EPIC-01` (`US-AUTH-01`), `EPIC-02` (`US-CUST-01`, `US-ADMIN-01`) | Spring Security / CORS / Session or JWT Config, Auth & Profile REST APIs (`/api/v1/auth/**`), Public Menu Catalog APIs (Search/Filter), Admin Category & Dish CRUD APIs, Quick Stock Toggle |
| **Member 2 (M2)** | Customer Reservation & Dine-in Ordering Lead | `EPIC-03` (`US-CUST-02`), `EPIC-04` (`US-CUST-03`), `EPIC-05` (`US-CUST-04`) | Pre-order Cart & Online Table Booking APIs (`/api/v1/customer/reservations/**`), Booking History, Active Dine-in Order Polling API (10s AJAX), In-meal Add-on Ordering, `'Request Bill'` Endpoint |
| **Member 3 (M3)** | Staff Operations, Billing & Admin Analytics Lead | `EPIC-04` (`US-STAFF-01`, `US-STAFF-02`), `EPIC-05` (`US-STAFF-03`), `EPIC-06` (`US-ADMIN-02`, `US-ADMIN-03`) | All-in-One Staff Dashboard APIs (`/api/v1/staff/**`: Table Grid, Booking Triage, Check-in, Dish Status Progression), Counter Checkout & Invoice Data API, Admin Table/Staff CRUD, Revenue & Top-Dishes Analytics APIs |

## 2. 4-Week Agile Sprint Plan (Week-by-Week Breakdown)

| Sprint / Week | Milestone Goal | Member 1 (Auth & Menu) | Member 2 (Reservation & Order) | Member 3 (Staff Ops & Admin) |
| :--- | :--- | :--- | :--- | :--- |
| **Week 1: Foundation, DB Schema & Core Setup** | Freeze MySQL schema, initialize Spring Boot JAR project, complete Auth & Master Data CRUD | - Initialize Maven Spring Boot JAR + `ApiResponse` / `GlobalExceptionHandler`<br>- Implement `User` entity, BCrypt Auth (`/api/v1/auth/**`), `SecurityConfig`, and `CorsConfig` | - Co-design MySQL schema & JPA Entities (`Reservation`, `Order`, `OrderItem`)<br>- Design `BookingRequest` / `CartItemDto` and reservation validation rules | - Co-design MySQL schema & `DiningTable` entity<br>- Build Admin Dining Table CRUD (`/api/v1/admin/tables/**`) & Staff Account CRUD (`/api/v1/admin/staff/**`) |
| **Week 2: Public Menu, Booking & Triage** | Complete pre-arrival loop: Browse Menu -> Pre-order & Book Table -> Staff Approves & Assigns Table | - Build Public Menu endpoints (`/api/v1/menu`) with category filter, search, and stock flags<br>- Build Admin Category & Dish CRUD (`/api/v1/admin/menu/**`) | - Build Customer Reservation + Pre-order API (`/api/v1/customer/reservations`)<br>- Validate future time slot, party size, & duplicate booking check | - Build Staff Dashboard Queue API (`/api/v1/staff/dashboard`)<br>- Implement Table Assignment (`RES_PENDING` -> `RES_CONFIRMED`) & Rejection endpoint |
| **Week 3: Dine-in Loop, Kitchen & Billing** | Complete in-restaurant loop: Guest Check-in -> Add-on Ordering -> Cooking/Served -> Counter Checkout | - Build Quick Stock Toggle endpoint for Staff/Admin (`PATCH /api/v1/staff/menu/{id}/stock`)<br>- Build User Profile & Order History endpoints | - Build Customer Active Order polling endpoint (`GET /api/v1/customer/orders/active`)<br>- Implement Add-on Dish endpoint, `PENDING` item cancel, & `'Request Bill'` endpoint | - Implement Guest Check-in (`Table OCCUPIED` + Order activation)<br>- Progress item statuses (`PENDING` -> `COOKING` -> `SERVED`)<br>- Counter Checkout (`PAID`) & Invoice DTO endpoint |
| **Week 4: Analytics, Hardening & Demo Prep** | Complete Admin Analytics, fix concurrency/edge cases, seed demo data, and rehearse defense | - Write integration tests for Auth RBAC (`HTTP 403`) & Soft Delete on Menu Items<br>- Verify Swagger / Postman collection & FE integration | - Harden edge cases: lock add-on ordering after `PAYMENT_REQUESTED`; validate sold-out cart items<br>- Prepare end-to-end Customer demo flow | - Build Admin Revenue & Top-5 Dishes Analytics endpoint (`/api/v1/admin/reports`)<br>- Verify `@Transactional` lock on table assignment & idempotent payment |

## 3. Spring Boot Backend Package & Directory Structure (No JSP/JSTL)

The Maven JAR project structure is partitioned by domain layer and member ownership so **M1**, **M2**, and **M3** can develop REST controllers, services, and DTOs concurrently without file collisions:

### A. Java Backend Package Tree (`src/main/java`)

```text
restaurant-management-backend/
├── pom.xml                                  # Packaging: jar (Web, Data JPA, MySQL, Security, Validation, DevTools)
└── src/
    ├── main/
    │   ├── java/com/university/restaurant/
    │   │   ├── RestaurantApplication.java
    │   │   │
    │   │   ├── config/                      # [M1]
    │   │   │   ├── SecurityConfig.java      # Role-based URL access rules (/api/v1/**) & BCryptPasswordEncoder
    │   │   │   ├── CorsConfig.java          # Allow decoupled Frontend origins (e.g., localhost:3000 / 5173)
    │   │   │   └── OpenApiConfig.java       # (Optional) Swagger UI for API testing
    │   │   │
    │   │   ├── common/                      # [Shared - Locked in Week 1]
    │   │   │   ├── enums/                   # RoleType, TableStatus, ReservationStatus, OrderStatus, OrderItemStatus
    │   │   │   ├── response/
    │   │   │   │   └── ApiResponse.java     # Standard JSON wrapper { success, message, data }
    │   │   │   └── exception/
    │   │   │       ├── BusinessException.java
    │   │   │       └── GlobalExceptionHandler.java  # @RestControllerAdvice returning JSON 400/403/404/409
    │   │   │
    │   │   ├── entity/                      # [Shared JPA Entities - Locked in Week 1]
    │   │   │   ├── User.java                # [M1]
    │   │   │   ├── Category.java            # [M1]
    │   │   │   ├── MenuItem.java            # [M1]
    │   │   │   ├── Reservation.java         # [M2]
    │   │   │   ├── Order.java               # [M2 / M3]
    │   │   │   ├── OrderItem.java           # [M2 / M3]
    │   │   │   └── DiningTable.java         # [M3]
    │   │   │
    │   │   ├── dto/
    │   │   │   ├── request/                 # [M1/M2/M3] LoginRequest, BookingRequest, AddOrderItemsRequest...
    │   │   │   └── response/                # [M1/M2/M3] MenuItemDto, OrderPollDto, RevenueSummaryDto...
    │   │   │
    │   │   ├── repository/                  # [M1/M2/M3] Spring Data JPA Repositories per Entity
    │   │   │
    │   │   ├── service/
    │   │   │   ├── AuthService.java, MenuService.java                  # [M1]
    │   │   │   ├── ReservationService.java, CustomerOrderService.java  # [M2]
    │   │   │   └── StaffOpsService.java, TableAdminService.java, ReportService.java # [M3]
    │   │   │
    │   │   └── controller/                  # All @RestController (/api/v1/**)
    │   │       ├── AuthController.java          # [M1] /api/v1/auth/**
    │   │       ├── MenuController.java          # [M1] /api/v1/menu/**, /api/v1/admin/menu/**
    │   │       ├── ReservationController.java   # [M2] /api/v1/customer/reservations/**
    │   │       ├── CustomerOrderController.java # [M2] /api/v1/customer/orders/**
    │   │       ├── StaffOpsController.java      # [M3] /api/v1/staff/**
    │   │       └── AdminPortalController.java   # [M3] /api/v1/admin/tables/**, /staff/**, /reports/**
```

### B. Resources & Test Hierarchy (`src/main/resources` & `src/test`)

```text
    │   └── resources/
    │       ├── application.properties           # MySQL datasource, JPA DDL, server.port=8080
    │       ├── data.sql                         # Seed: 1 Admin, 2 Staff, 10 Dining Tables, 15 Menu Items
    │       ├── templates/                       # (Only if using Thymeleaf; omit if FE is fully decoupled)
    │       └── static/                          # (Only if serving static HTML/CSS/JS directly from Spring Boot)
    │
    └── test/
        └── java/com/university/restaurant/      # Unit & Integration Tests (JUnit 5 + MockMvc)
```

## 4. Git Workflow & Collaboration Rules for 3 Students

* **Branching Strategy:** Maintain `main` for verified weekly releases, `develop` for team integration, and member feature branches (`feat/m1-auth-menu`, `feat/m2-reservation-order`, `feat/m3-staff-admin`).
* **Contract-First Synchronization (Day 1–2 of Week 1):** All 3 members co-review and merge the 7 JPA Entities, 5 Status Enums, and `ApiResponse` wrapper into `develop` by Day 2 of Week 1 so everyone can build REST Controllers, Services, and DTOs independently without schema conflicts.
* **Shared Polling API Contract:** Standardize JSON payload structure in `OrderPollDto` by the end of Week 2 so M2's Customer Active Order endpoint and M3's Staff Dashboard endpoint remain consistent for Frontend 10-second polling.
