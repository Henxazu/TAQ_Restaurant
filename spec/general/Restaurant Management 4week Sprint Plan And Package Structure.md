# 4-Week Sprint Plan & Spring Boot Package Structure
**System:** Basic Restaurant Management Web Application  
**Course:** Web Application Development (University Capstone Project)  
**Team Size & Timeline:** 3 Students | 4 Weeks  
**Technology Stack:** Java 17+, Spring Boot 3.x (Spring MVC / J2EE), JSP & JSTL, MySQL 8.0, Apache Maven (WAR), Apache Tomcat 10.x  

## 1. Team Role Assignment & Module Ownership (3 Students)

To prevent Git merge conflicts and balance workload across the 4-week timeline, each team member owns distinct vertical slices (`Controller -> Service -> Repository -> JSP Views`) while sharing the core JPA Entities locked in Week 1:

| Member | Module Ownership | Mapped Epics & Stories | Core Deliverables |
| :--- | :--- | :--- | :--- |
| **Member 1 (M1)** | Core Foundation, Auth & Menu Catalog Lead | `EPIC-01` (`US-AUTH-01`), `EPIC-02` (`US-CUST-01`, `US-ADMIN-01`) | Spring Security / Session Interceptor, Login/Register/Profile JSPs, Public Menu Catalog (Search/Filter), Admin Category & Dish CRUD, Quick Stock Toggle |
| **Member 2 (M2)** | Customer Reservation & Dine-in Ordering Lead | `EPIC-03` (`US-CUST-02`), `EPIC-04` (`US-CUST-03`), `EPIC-05` (`US-CUST-04`) | Session Pre-order Cart, Online Table Booking Form, Customer Booking History, Active Dine-in Order Tracking (10s AJAX Polling), In-meal Add-on Ordering, `'Request Bill'` Trigger |
| **Member 3 (M3)** | Staff Operations, Billing & Admin Analytics Lead | `EPIC-04` (`US-STAFF-01`, `US-STAFF-02`), `EPIC-05` (`US-STAFF-03`), `EPIC-06` (`US-ADMIN-02`, `US-ADMIN-03`) | All-in-One Staff Dashboard (Table Grid, Booking Approval, Guest Check-in, Dish Status Progression), Counter Checkout & Printable Invoice, Admin Table/Staff CRUD, Revenue Analytics |

## 2. 4-Week Agile Sprint Plan (Week-by-Week Breakdown)

| Sprint / Week | Milestone Goal | Member 1 (Auth & Menu) | Member 2 (Reservation & Order) | Member 3 (Staff Ops & Admin) |
| :--- | :--- | :--- | :--- | :--- |
| **Week 1: Foundation, DB Schema & Core Setup** | Freeze MySQL schema, initialize Spring Boot WAR project, complete Auth & Master Data CRUD | - Initialize Maven Spring Boot WAR + JSP/JSTL + Bootstrap 5 layout (`header.jsp`, `footer.jsp`)<br>- Implement `User` entity, BCrypt Auth, and Role Interceptor (`CUSTOMER`, `STAFF`, `ADMIN`) | - Co-design MySQL schema & JPA Entities (`Reservation`, `Order`, `OrderItem`)<br>- Build Session `PreOrderCart` utility & Cart JSP drawer | - Co-design MySQL schema & `DiningTable` entity<br>- Build Admin Dining Table CRUD (`/admin/tables`) & Staff Account CRUD (`/admin/staff`) |
| **Week 2: Public Menu, Booking & Triage** | Complete pre-arrival loop: Browse Menu -> Pre-order & Book Table -> Staff Approves & Assigns Table | - Build Public Menu (`/menu`) with category tabs, search, and stock badges<br>- Build Admin Category & Dish CRUD (`/admin/menu`) | - Build Customer Reservation + Pre-order form (`/customer/reservations/new`)<br>- Validate future time slot, party size, & duplicate booking check | - Build Staff Dashboard Reservation Queue (`/staff/dashboard`)<br>- Implement Table Assignment (`RES_PENDING` -> `RES_CONFIRMED`) & Rejection |
| **Week 3: Dine-in Loop, Kitchen & Billing** | Complete in-restaurant loop: Guest Check-in -> Add-on Ordering -> Cooking/Served -> Counter Checkout | - Build Quick Stock Toggle endpoint for Staff/Admin<br>- Build User Profile update & Customer Order History detail page | - Build Customer Active Order view (`/customer/active-order`) with 10s AJAX polling<br>- Implement Add-on Dish ordering, `PENDING` cancel, & `'Request Bill'` | - Implement Guest Check-in (`Table OCCUPIED` + Order activation)<br>- Progress item statuses (`PENDING` -> `COOKING` -> `SERVED`)<br>- Counter Checkout (`PAID`) & Printable Invoice |
| **Week 4: Analytics, Hardening & Demo Prep** | Complete Admin Analytics, fix concurrency/edge cases, seed demo data, and rehearse defense | - Test RBAC route protection (`HTTP 403`) & Soft Delete on Menu Items<br>- Polish responsive mobile UI (`375px–430px`) for Customer JSPs | - Harden edge cases: lock add-on ordering after `PAYMENT_REQUESTED`; validate sold-out cart items<br>- Prepare Customer demo script | - Build Admin Revenue & Top-5 Dishes Analytics (`/admin/reports`) with Chart.js<br>- Verify `@Transactional` lock on table assignment & idempotent payment |

## 3. Spring Boot + JSP/JSTL Package & Directory Structure

The Maven WAR project structure is partitioned by domain layer and member ownership so **M1**, **M2**, and **M3** can develop controllers, services, and JSP views concurrently without file collisions:

### A. Java Backend Package Tree (`src/main/java`)

```text
restaurant-management-system/
├── pom.xml                        # WAR: Spring Boot 3, Jasper, JSTL, MySQL, Security
└── src/main/java/com/university/restaurant/
    ├── RestaurantApplication.java
    ├── ServletInitializer.java    # Tomcat WAR initializer
    ├── config/                    # [M1] SecurityConfig, WebMvcConfig
    ├── common/                    # [Shared - Locked Week 1]
    │   ├── enums/                 # RoleType, TableStatus, ReservationStatus, OrderStatus, OrderItemStatus
    │   └── exception/             # GlobalExceptionHandler.java
    ├── entity/                    # [Shared Entities - Locked Week 1]
    │   ├── User, Category, MenuItem       # [M1]
    │   ├── Reservation, Order, OrderItem  # [M2 / M3]
    │   └── DiningTable                    # [M3]
    ├── dto/
    │   ├── cart/                  # [M2] CartItemDto, PreOrderCartSession
    │   ├── polling/               # [M2/M3] OrderStatusPollResponse (JSON for 10s AJAX)
    │   └── report/                # [M3] RevenueSummaryDto, TopDishDto
    ├── repository/                # [M1/M2/M3] Spring Data JPA Repositories per Entity
    ├── service/
    │   ├── AuthService, MenuService                  # [M1]
    │   ├── ReservationService, CustomerOrderService  # [M2]
    │   └── StaffOpsService, TableAdminService, ReportService # [M3]
    └── controller/
        ├── AuthController, MenuController            # [M1]
        ├── ReservationController, CustomerOrderController # [M2]
        └── StaffDashboardController, AdminPortalController # [M3]
```

### B. Resources & JSP View Hierarchy (`src/main/resources` & `webapp`)

```text
src/main/
├── resources/
│   ├── application.properties     # MySQL connection, JPA settings, JSP ViewResolver
│   ├── data.sql                   # Seed: 1 Admin, 2 Staff, 10 Dining Tables, 15 Menu Items
│   └── static/
│       ├── css/                   # custom.css + Bootstrap 5 assets
│       └── js/
│           ├── customer-poll.js   # [M2] 10s Fetch API polling (/customer/active-order)
│           └── staff-poll.js      # [M3] 10s Fetch API polling (/staff/dashboard)
└── webapp/WEB-INF/views/
    ├── layout/                    # [M1] header.jsp, navbar.jsp, footer.jsp, alerts.jsp
    ├── auth/                      # [M1] login.jsp, register.jsp, profile.jsp
    ├── menu/                      # [M1] public-menu.jsp, admin-menu-list.jsp, admin-menu-form.jsp
    ├── customer/                  # [M2] cart-drawer.jsp, reservation-form.jsp, my-reservations.jsp, active-order.jsp
    ├── staff/                     # [M3] dashboard.jsp, checkout-modal.jsp, invoice-print.jsp
    └── admin/                     # [M3] table-list.jsp, staff-list.jsp, revenue-report.jsp
```

## 4. Git Workflow & Collaboration Rules for 3 Students

* **Branching Strategy:** Maintain `main` for verified weekly releases, `develop` for team integration, and member feature branches (`feat/m1-auth-menu`, `feat/m2-reservation-order`, `feat/m3-staff-admin`).
* **Contract-First Synchronization (Day 1–2 of Week 1):** All 3 members co-review and merge the 7 JPA Entities and 5 Status Enums into `develop` by Day 2 of Week 1 so everyone can build Controllers, Services, and JSPs independently without schema conflicts.
* **Shared Polling API Contract:** Standardize JSON payload structure in `OrderStatusPollResponse.java` by the end of Week 2 so M2's Customer Active Order screen and M3's Staff Dashboard share the same 10-second polling endpoint contract.
