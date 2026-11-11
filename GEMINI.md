# GEMINI.md — Project Context & AI Coding Guidelines

> **Purpose:** File cấu hình ngữ cảnh gốc (Root Context File) dành cho **Gemini CLI / Gemini Code Assist** và các thành viên trong nhóm. Đặt file này tại thư mục gốc của repository (`restaurant-qr-pos-monorepo/GEMINI.md`) để AI luôn nắm vững kiến trúc, quy tắc nghiệp vụ, chuẩn code và phân vùng Monorepo trước khi sinh code hoặc debug.

## 1. Project Identity & Overview

- **Project Name:** TAQ_Restaurant
- **Team Size & Timeline:** 3 Students (`SV1`, `SV2`, `SV3`) — 4 Sprints (4 Weeks)
- **Architecture Pattern:** Modular Monolith (Package-by-Feature) combining **Hybrid Server-Side Rendering (JSP/JSTL)** for initial page views and **RESTful JSON APIs + Real-Time WebSockets (STOMP over SockJS)** for dynamic state updates without page reloads.
- **Reference Documents in Repo:**
  - `spec/general/general_spec.md`: Full System & Functional Specification (Actors, RBAC, 14 User Stories, Sequence Diagram, 10-Table ERD, REST APIs & WebSocket Events).
  - `spec/general/monorepo_architecture_and_sprint.md`: Monorepo folder structure & 4-Sprint task breakdown.

## 2. Tech Stack & Strict Version Constraints (CRITICAL FOR CODE GENERATION)

When generating Java, Maven, SQL, or JSP code, **ALWAYS** adhere to the following stack and avoid deprecated legacy APIs:

| Layer | Technology / Library | Mandatory Rules & Gotchas for AI Code Generation |
| :--- | :--- | :--- |
| **Language & Build** | Java 17+ / Maven (`packaging: war`) | Must use `<packaging>war</packaging>` and include `ServletInitializer extends SpringBootServletInitializer` so Embedded/External Tomcat can compile JSP files. |
| **Backend Framework** | Spring Boot 3.2+ (`spring-boot-starter-web`, `data-jpa`, `security`, `websocket`, `validation`) | **CRITICAL:** Spring Boot 3 uses **`jakarta.*`** namespace (`jakarta.persistence.*`, `jakarta.servlet.*`, `jakarta.validation.*`). **NEVER** import `javax.persistence.*` or `javax.servlet.*`. |
| **View Engine (SSR)** | JSP + JSTL (`tomcat-embed-jasper`, `jakarta.servlet.jsp.jstl-api`, `org.glassfish.web.jakarta.servlet.jsp.jstl`) | Place all `.jsp` files inside `src/main/webapp/WEB-INF/views/**`. **NEVER** place JSP files inside `src/main/resources/templates/` (that directory is only for Thymeleaf). Use Jakarta JSTL 3.0 URI: `<%@ taglib prefix="c" uri="jakarta.tags.core" %>` and `<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>`. |
| **Database & Migration** | MySQL 8.x + Flyway (`flyway-core`, `flyway-mysql`) | Set `spring.jpa.hibernate.ddl-auto=validate` (or `none` during initial dev) and let Flyway manage schema via `src/main/resources/db/migration/V1__init_schema.sql` & `V2__seed_initial_data.sql`. |
| **Authentication** | Spring Security 6 + JWT (`jjwt 0.12.x`) + HMAC-SHA256 QR Signature | Use Spring Security 6 Lambda DSL (`SecurityFilterChain` bean, `http.authorizeHttpRequests(...)`). **NEVER** use deprecated `WebSecurityConfigurerAdapter`. |
| **Real-Time Messaging** | Spring WebSocket (STOMP + SockJS) | Handshake endpoint: `/ws-restaurant` (`.withSockJS()`). Broker prefix: `/topic`. App prefix: `/app`. Inject `SimpMessagingTemplate` to broadcast events. |
| **Utilities & UI** | Lombok, ZXing (`com.google.zxing` for QR), Bootstrap 5, Chart.js | Always use `BigDecimal` for money/prices in Java and `DECIMAL(12,2)` in MySQL. **NEVER** use `Float` or `Double` for currency calculations. |

## 3. Monorepo Directory Structure & Team Ownership Rules

To prevent Git merge conflicts across the 3 team members, respect the strict module and file boundaries below:

```text
src/main/java/com/restaurant/qrpos/
├── common/             # Shared ApiResponse<T>, WsEventMessage<T>, Enums, GlobalExceptionHandler, Utils
├── config/             # SecurityConfig (SV1), WebSocketConfig (SV2), WebMvcConfig (SV3)
├── security/           # JwtTokenProvider, JwtCookieAuthenticationFilter, GuestSessionAuthenticationFilter (SV1)
├── entity/             # 10 JPA Entities mapped 1:1 to V1__init_schema.sql (Locked in Sprint 1)
└── modules/
    ├── auth/           # [SV1] Staff Login/Logout, Role-based redirect (/kds, /pos, /waiter/tables, /admin/dashboard)
    ├── table/          # [SV1] QR Scan HMAC verify, TableSession lifecycle, Live Floor Map & Table Transfer
    │                   # [SV3] Admin Area/Table CRUD & Printable QR Code Sheet Generator (ZXing)
    ├── menu/           # [SV3] Category & MenuItem CRUD, Multipart Image Upload, Soft Delete
    ├── ordering/       # [SV2] Guest QR Menu browsing, Multi-batch Order submission (batch_no), Call Staff/Bill
    ├── kds/            # [SV2] Kitchen Display FIFO Queue, Status transitions (COOKING/READY/SERVED), 86 Out-of-Stock toggle
    ├── pos/            # [SV1] POS Bill Summary, Discount & VAT calculation, Checkout Transaction, 80mm Receipt Print
    └── admin/          # [SV3] Staff Account CRUD (RBAC) & Revenue/Top-Dishes Analytics Dashboard
```

### Frontend JSP & Static Asset Isolation
- **JSP Views (`src/main/webapp/WEB-INF/views/`):**
  - `fragments/`: Shared header, sidebar, toast alerts (`_header.jsp`, `_toast.jsp`).
  - `auth/` (**SV1**): `login.jsp`, `access-denied.jsp`.
  - `guest/` (**SV2**): `qr-menu.jsp`, `order-tracking.jsp`, `session-closed.jsp`.
  - `kds/` (**SV2**): `kitchen-display.jsp`.
  - `waiter/` (**SV1 & SV2**): `floor-plan.jsp` (SV1), `ready-pickup.jsp` (SV2).
  - `pos/` (**SV1**): `pos-terminal.jsp`, `invoice-print-80mm.jsp`.
  - `admin/` (**SV3**): `dashboard.jsp`, `menu-crud.jsp`, `table-qr.jsp`, `staff-crud.jsp`.
- **Static JS (`src/main/webapp/resources/static/js/`):**
  - Core helpers: `core/api-client.js` (Fetch wrapper), `core/ws-client.js` (STOMP/SockJS auto-reconnect).
  - Feature scripts (1 file per screen): `guest-ordering.js`, `kds-realtime.js`, `waiter-operations.js`, `pos-checkout.js`, `admin-management.js`.

## 4. Core Business Rules & State Machine Invariants

Every Service method generated or modified must enforce these domain invariants:

### 4.1. Dual Authentication & QR Security (`US-06`, `US-14`)
1. **Staff Authentication:**
   - `POST /api/v1/auth/login` verifies `BCrypt` password and sets an `HttpOnly` cookie named `STAFF_ACCESS_TOKEN` containing a signed JWT (`sub: username`, `role: ROLE_...`).
   - `JwtCookieAuthenticationFilter` extracts `STAFF_ACCESS_TOKEN` from cookies (or `Authorization: Bearer` header for Postman testing) and populates `SecurityContextHolder`.
2. **Guest QR Table Session:**
   - Physical table QR URL format: `/qr/scan?tableCode={code}&sig={hmacSha256}`.
   - Where `sig = HMAC_SHA256(tableCode, table.qrSecretKey)`.
   - When a guest scans a valid QR:
     - If `table.status == AVAILABLE`: Create a new `TableSession` (`status = ACTIVE`, `session_token = UUID`), create a new `Order` (`status = OPEN`, `subtotal_amount = 0`), update `table.status = OCCUPIED`, and broadcast `TABLE_STATUS_CHANGED` to `/topic/tables`.
     - If `table.status == OCCUPIED` or `BILL_REQUESTED`: Attach the guest to the currently `ACTIVE` `TableSession` of that table so multiple people at the same table share the same live order.
     - Set cookie `GUEST_SESSION_TOKEN` (`HttpOnly`) and redirect to `/qr/menu`.
   - When Cashier completes checkout (`POST /api/v1/pos/orders/{orderId}/checkout`), `TableSession.status` becomes `CLOSED`. `GuestSessionAuthenticationFilter` must reject any subsequent API calls using a `CLOSED` `session_token` with HTTP `401/403`.

### 4.2. State Machines & Valid Transitions
- **`RestaurantTable.status`:** `AVAILABLE` $\rightarrow$ `OCCUPIED` $\rightarrow$ `BILL_REQUESTED` $\rightarrow$ `AVAILABLE` (or `CLEANING`).
- **`Order.status`:** `OPEN` $\rightarrow$ `BILL_REQUESTED` $\rightarrow$ `PAID` (or `CANCELLED`).
- **`OrderItem.status`:**
  - Normal flow: `PENDING` $\rightarrow$ `COOKING` (by Kitchen) $\rightarrow$ `READY` (by Kitchen) $\rightarrow$ `SERVED` (by Waiter).
  - Cancellation rule (`US-10`): Customer or Waiter can cancel an item **ONLY IF** `status == PENDING`. Once `status == COOKING`, customer/waiter cancellation is blocked (throw `BusinessException("ITEM_ALREADY_COOKING")`). Kitchen can reject an item (`CANCELLED`) if ingredients run out, requiring a non-empty `cancel_reason`.

### 4.3. Financial & Historical Integrity Rules (`US-01`, `US-07`, `US-11`, `US-12`)
1. **Batch Numbering (`batch_no`):**
   - Each time a table submits cart items (`POST /api/v1/guest/orders/items`), calculate `nextBatchNo = COALESCE(MAX(batch_no), 0) + 1` for that `order_id`.
2. **Price Snapshot (`unit_price`):**
   - `OrderItem.unitPrice` MUST copy `MenuItem.price` at the moment the order is placed. Never join `menu_items.price` to calculate historical order or invoice totals.
3. **Subtotal & Checkout Formula:**
   - `Order.subtotalAmount` = $\sum (\text{quantity} \times \text{unitPrice})$ for all `OrderItem` rows in that order where `status != 'CANCELLED'`.
   - At POS Checkout:
     $$\text{taxAmount} = \text{ROUND}\left((\text{subtotalAmount} - \text{discountAmount}) \times \frac{\text{taxPercent}}{100}, 2\right)$$
     $$\text{finalAmount} = (\text{subtotalAmount} - \text{discountAmount}) + \text{taxAmount}$$
4. **Soft Delete (`is_deleted`):**
   - Never execute `DELETE FROM menu_items`. Always set `menuItem.setDeleted(true)` and filter guest/POS queries with `WHERE is_deleted = false`.

## 5. Coding Conventions & API / WebSocket Contracts

### 5.1. Standard REST API Response Wrapper
All `@RestController` methods under `/api/v1/**` must return `ResponseEntity<ApiResponse<T>>`:

```java
@Getter @Setter @Builder @AllArgsConstructor @NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private String errorCode;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    public static <T> ApiResponse<T> ok(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder().success(false).errorCode(errorCode).message(message).build();
    }
}
```

### 5.2. Standard WebSocket Event Contract & Topics
Always wrap STOMP payloads in `WsEventMessage<T>` and publish **after** database changes are persisted:

| Event Name | Topic Destination | Triggered When |
| :--- | :--- | :--- |
| `NEW_ORDER_BATCH` | `/topic/kitchen/orders` | Guest or Waiter submits a new batch of `OrderItem`s |
| `ITEM_STATUS_CHANGED` | `/topic/orders/{orderId}` | Kitchen (`COOKING`, `READY`, `CANCELLED`) or Waiter (`SERVED`) updates an item |
| `ITEM_READY_TO_SERVE` | `/topic/waiter/notifications` | Kitchen marks an item `READY` or `CANCELLED` |
| `MENU_AVAILABILITY_UPDATED` | `/topic/menu-status` | Kitchen or Admin toggles `MenuItem.isAvailable` (Out-of-stock 86'd) |
| `TABLE_STATUS_CHANGED` | `/topic/tables` | Table opens session, transfers, requests bill, or finishes POS checkout |
| `CUSTOMER_CALL_STAFF` | `/topic/staff/alerts` | Guest clicks "Call Waiter" (`ASSIST`) or "Request Bill" (`BILL`) |
| `ORDER_PAID_CLOSED` | `/topic/orders/{orderId}` | Cashier completes POS checkout and closes `TableSession` |

### 5.3. JSP View & Static Resource Best Practices
1. Always prefix internal links, AJAX calls, and static assets in `.jsp` files with `${pageContext.request.contextPath}`:
   ```jsp
   <script>const CONTEXT_PATH = '${pageContext.request.contextPath}';</script>
   <script src="${pageContext.request.contextPath}/resources/static/js/core/api-client.js"></script>
   ```
2. Include `<%@ page contentType="text/html;charset=UTF-8" language="java" %>` at line 1 of every `.jsp` file so Vietnamese characters display without encoding issues.
3. Separate View Controllers (`@Controller` returning JSP view names like `"guest/qr-menu"`) from Data Controllers (`@RestController` prefixed with `@RequestMapping("/api/v1/...")`).

## 6. Local Development & Commands Cheat Sheet

- **Start MySQL 8 & phpMyAdmin via Docker:**
  ```bash
  docker compose up -d
  ```
- **Run Spring Boot Application (with JSP live reload in dev):**
  ```bash
  mvn clean spring-boot:run
  ```
- **Package Deployable WAR for External Tomcat:**
  ```bash
  mvn clean package -DskipTests
  ```
- **Run Unit & Integration Tests:**
  ```bash
  mvn test
  ```

## 7. Quick Prompt Templates for Team Members (Copy-Paste to Gemini)

Khi bắt đầu code từng task trong Sprint, mỗi bạn có thể copy mẫu câu lệnh dưới đây gửi cho Gemini:

- **Dành cho SV1 (Auth, TableSession, POS):**
  > *"Đọc file `GEMINI.md` và `docs/system-specification-en.md`. Tôi là **SV1**, đang làm task **[Mã Task, VD: S2-T1 - US-06]**. Hãy viết code đầy đủ cho các class trong package `com.restaurant.qrpos.modules.table` và `security` tuân thủ chuẩn `jakarta.*`, `ApiResponse<T>`, và phát sự kiện WebSocket `/topic/tables`."*
- **Dành cho SV2 (Guest QR Ordering, WebSocket, KDS & Waiter):**
  > *"Đọc file `GEMINI.md` và `docs/system-specification-en.md`. Tôi là **SV2**, đang làm task **[Mã Task, VD: S3-T3 - US-08]**. Hãy viết Backend API trong `com.restaurant.qrpos.modules.kds`, trang JSP `WEB-INF/views/kds/kitchen-display.jsp` và file JS `kds-realtime.js` kết nối STOMP WebSocket theo đúng cấu trúc Monorepo."*
- **Dành cho SV3 (Menu CRUD, QR Generator, Staff & Revenue Analytics):**
  > *"Đọc file `GEMINI.md` và `docs/system-specification-en.md`. Tôi là **SV3**, đang làm task **[Mã Task, VD: S1-T5 - US-01]**. Hãy viết code CRUD món ăn có upload ảnh Multipart và Soft Delete trong `com.restaurant.qrpos.modules.menu` kèm trang `WEB-INF/views/admin/menu-crud.jsp`."*
