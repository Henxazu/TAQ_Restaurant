# [ĐẶC TẢ HỆ THỐNG THIẾT KẾ UI/UX & BẢN MẪU TƯƠNG TÁC FIGMA](https://www.figma.com/design/ThbxRZumaqY3unZGK5evkl/Untitled?node-id=0-1&t=ca9uMVGjyKaWtvAw-1)

**Dự án:** Hệ thống Web Quản lý Nhà hàng Cơ bản (Basic Restaurant Management Web Application)  
**Môn học:** Web Application Development (Đồ án Môn học / Capstone Project)  
**Định hướng thẩm mỹ:** Ấm cúng, Hiện đại, Kích thích vị giác (*Warm & Modern Dining*)  
**Nền tảng công nghệ:** Java 17+, Spring Boot 3.x (Spring MVC), JSP & JSTL, Bootstrap 5, MySQL 8.0  
**Phân hệ người dùng:** Khách hàng (`ROLE_CUSTOMER`), Nhân viên Vận hành (`ROLE_STAFF`), Quản trị viên (`ROLE_ADMIN`)

## CHẶNG 1: THIẾT LẬP DESIGN SYSTEM & FIGMA FOUNDATIONS

Toàn bộ thông số kỹ thuật dưới đây được chuẩn hóa để thiết lập trực tiếp vào **Figma Variables** và **Local Styles**, đồng thời tương thích 100% với biến CSS (`:root`) của Bootstrap 5 khi triển khai giao diện JSP/JSTL.

### 1.1. Bảng màu (Color Palette & Semantic Tokens)

Tạo một Variable Collection trên Figma tên là `01_Color_Tokens`. Các cặp màu chữ và nền đều đạt tỷ lệ tương phản theo chuẩn **WCAG 2.1 AA/AAA** để đảm bảo khả năng đọc rõ ràng dưới ánh đèn ấm tại nhà hàng hoặc trên màn hình máy POS thu ngân.

#### A. Brand, Neutral & Text Colors (Màu Thương hiệu, Nền & Chữ)

| Nhóm | Tên Token (Figma Variable) | Mã HEX | Độ tương phản (WCAG) | Ứng dụng thực tế trên UI & PRD |
| :---- | :---- | :---- | :---- | :---- |
| **Primary** | `color/primary/default` | `#C84B31` | **4.82:1** (với `#FFFFFF` \- AA) | Nút CTA chính (*Đặt bàn ngay*, *Thêm vào giỏ*, *Xác nhận thanh toán*), Active Tab, điểm nhấn thương hiệu (Terracotta). |
| **Primary** | `color/primary/hover` | `#A63820` | **6.75:1** (với `#FFFFFF` \- AA) | Trạng thái Hover / Pressed của nút Primary (Warm Crimson đậm). |
| **Primary** | `color/primary/subtle` | `#FBECE8` | **11.8:1** (với `#A63820` \- AAA) | Nền thẻ danh mục đang chọn, nền highlight thông báo nhẹ, hover dòng bảng dữ liệu. |
| **Secondary** | `color/secondary/default` | `#D97706` | **4.60:1** (với `#1F1A17` \- AA) | Nhãn *Best-seller*, ngôi sao đánh giá, điểm nhấn phụ (Warm Amber / Ochre). |
| **Secondary** | `color/secondary/subtle` | `#FEF3C7` | **10.9:1** (với `#92400E` \- AAA) | Nền badge khuyến mãi hoặc ghi chú đặc biệt (`Special Notes`). |
| **Surface** | `color/bg/canvas` | `#FAF7F2` | Nền tổng thể | Màu nền chính toàn trang (Warm Ivory) — tạo cảm giác ấm áp, sạch sẽ, không gây mỏi mắt. |
| **Surface** | `color/bg/surface-card` | `#FFFFFF` | Nền thẻ | Nền của `Dish Card`, `Order Ticket`, Modal, Bảng dữ liệu để nổi bật trên nền Canvas `#FAF7F2`. |
| **Surface** | `color/bg/elevated` | `#F3EDE4` | Nền phụ | Nền Sidebar, Header phụ, ô Input disabled, hoặc vùng phân cách khu vực bàn (`Zone`). |
| **Text** | `color/text/primary` | `#1F1A17` | **15.8:1** (trên `#FAF7F2` \- AAA) | Tiêu đề, tên món ăn, tổng tiền hóa đơn, nội dung chính (Deep Espresso / Charcoal). |
| **Text** | `color/text/secondary` | `#5C534E` | **6.90:1** (trên `#FAF7F2` \- AA) | Mô tả món ăn, nhãn phụ, thời gian đặt bàn, metadata (Warm Slate). |
| **Text** | `color/text/muted` | `#8C827A` | **3.65:1** (Large Text / Icon) | Placeholder trong ô Input, trạng thái hết hàng (`is_available = false`), đường kẻ phân cách. |
| **Border** | `color/border/default` | `#E6DFD5` | — | Viền thẻ món ăn, đường kẻ bảng dữ liệu, viền form input mặc định. |
| **Border** | `color/border/focus` | `#C84B31` | — | Viền khi người dùng click vào ô nhập liệu (kết hợp Drop Shadow `4px` `#FBECE8`). |

#### B. Semantic & Status Colors (Trạng thái Bàn, Đơn đặt bàn & Món ăn bám sát State Machine trong PRD)

Để nhân viên trên màn hình `/staff/dashboard` (làm mới mỗi 10s qua AJAX) nhận diện trạng thái tức thì, mỗi trạng thái được chuẩn hóa theo bộ 3 màu: **Fill (Nền nhạt) \- Border (Viền) \- Text/Icon (Chữ đậm)**.

| Phân nhóm PRD | Trạng thái (Enum trong PRD) | Tên Token trên Figma | Nền Badge/Node (`Fill`) | Viền (`Border`) | Chữ & Icon (`Text`) | Độ tương phản Text/Fill |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **Trạng thái Bàn (`Table`)** | **Trống** (`AVAILABLE`) | `status/table/available` | `#ECFDF5` | `#6EE7B7` | `#065F46` (Xanh Sage đậm) | **8.4:1** (AAA) |
| **Trạng thái Bàn (`Table`)** | **Đã đặt trước** (`RESERVED`) | `status/table/reserved` | `#FFFBEB` | `#FCD34D` | `#92400E` (Vàng Hổ phách) | **7.6:1** (AAA) |
| **Trạng thái Bàn (`Table`)** | **Đang phục vụ** (`OCCUPIED`) | `status/table/occupied` | `#FBECE8` | `#F5A391` | `#9E2A1B` (Đỏ Terracotta) | **7.1:1** (AAA) |
| **Trạng thái Bàn (`Table`)** | **Chờ thanh toán** (`PAYMENT_REQUESTED`) | `status/table/payment-req` | `#F5F3FF` | `#C4B5FD` | `#5B21B6` (Tím Hoàng gia) | **8.2:1** (AAA) |
| **Trạng thái Bàn (`Table`)** | **Bảo trì** (`MAINTENANCE`) | `status/table/maintenance` | `#F3F2F0` | `#D6D3CD` | `#57534E` (Xám Tro) | **6.8:1** (AA) |
| **Trạng thái Món (`Order_Item`)** | **Chờ chế biến** (`PENDING`) | `status/item/pending` | `#FFF7ED` | `#FDBA74` | `#9A3412` (Cam Đất) | **6.9:1** (AA) |
| **Trạng thái Món (`Order_Item`)** | **Đang nấu** (`COOKING`) | `status/item/cooking` | `#EFF6FF` | `#93C5FD` | `#1E40AF` (Xanh Lam) | **7.8:1** (AAA) |
| **Trạng thái Món (`Order_Item`)** | **Đã lên món / Đã thu tiền** (`SERVED` / `PAID`) | `status/item/served` | `#ECFDF5` | `#6EE7B7` | `#065F46` (Xanh Ngọc) | **8.4:1** (AAA) |
| **Trạng thái Món (`Order_Item`)** | **Đã hủy / Từ chối** (`CANCELLED` / `REJECTED`) | `status/item/cancelled` | `#FEF2F2` | `#FCA5A5` | `#991B1B` (Đỏ Cảnh báo) | **7.9:1** (AAA) |

### 1.2. Kiểu chữ (Typography Scale)

* **Heading Font (Trang Customer, Tên nhà hàng, Danh mục món):** **Playfair Display** — mang nét ẩm thực tinh tế, sang trọng, hỗ trợ tiếng Việt hoàn chỉnh. *(Trên màn hình Staff POS & Admin Dashboard, sử dụng **Plus Jakarta Sans Bold** cho tiêu đề để tối ưu tốc độ quét thông tin).*  
* **Body & UI Font (Nội dung, nút bấm, bảng dữ liệu, giá tiền):** **Plus Jakarta Sans** — hiện đại, lòng chữ rộng, hiển thị rõ nét trên cả màn hình điện thoại nhỏ.  
* **Numeric Alignment (Mã bàn `T-01`, Mã đơn `#ORD-1024`, Giá tiền, Đồng hồ đếm giờ):** Bật thuộc tính OpenType `tabular-nums` (Tabular Figures) của **Plus Jakarta Sans** để các con số thẳng cột tuyệt đối trong bảng và hóa đơn.

#### Bảng thông số Figma Text Styles (`1rem = 16px`)

| Style Name (Figma) | Font Family | Font Weight | Desktop Size (`px`/`rem`) | Mobile Size (`px`/`rem`) | Line Height (`px`/`%`) | Letter Spacing | Ứng dụng trên giao diện |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| `Heading/H1-Hero` | Playfair Display | Bold (`700`) | `40px` (`2.5rem`) | `28px` (`1.75rem`) | `48px` / `36px` (`120%`) | `-0.02em` | Banner trang chủ Customer, Tiêu đề trang Đặt bàn. |
| `Heading/H2-Section` | Playfair Display / Plus Jakarta Sans | Bold (`700`) | `28px` (`1.75rem`) | `22px` (`1.375rem`) | `36px` / `28px` (`128%`) | `-0.015em` | Tiêu đề nhóm thực đơn (*Món Chính*, *Khai Vị*), Tiêu đề trang Admin. |
| `Heading/H3-CardTitle` | Plus Jakarta Sans | SemiBold (`600`) | `20px` (`1.25rem`) | `18px` (`1.125rem`) | `28px` / `26px` (`140%`) | `-0.01em` | Tiêu đề Modal, Tên khu vực bàn, Tiêu đề biểu đồ doanh thu. |
| `Heading/H4-DishName` | Plus Jakarta Sans | SemiBold (`600`) | `16px` (`1.0rem`) | `16px` (`1.0rem`) | `24px` (`150%`) | `0em` | Tên món ăn trên `Dish Card`, Mã bàn `T-01` trên sơ đồ. |
| `Body/Large-Price` | Plus Jakarta Sans (`tabular-nums`) | Bold (`700`) | `18px` (`1.125rem`) | `16px` (`1.0rem`) | `24px` (`135%`) | `0em` | Giá món ăn (`185.000đ`), Tổng tiền tạm tính ở thanh Sticky Bar. |
| `Body/Medium-Default` | Plus Jakarta Sans | Regular (`400`) | `15px` (`0.9375rem`) | `14px` (`0.875rem`) | `22px` / `20px` (`145%`) | `0em` | Mô tả món ăn, nội dung bảng dữ liệu, ghi chú đặt bàn. |
| `UI/Button-Label` | Plus Jakarta Sans | SemiBold (`600`) | `14px` (`0.875rem`) | `14px` (`0.875rem`) | `20px` (`142%`) | `+0.01em` | Chữ trên mọi nút bấm (Button), Tab chuyển đổi, Menu điều hướng. |
| `UI/Caption-Badge` | Plus Jakarta Sans | SemiBold (`600`) | `12px` (`0.75rem`) | `12px` (`0.75rem`) | `16px` (`133%`) | `+0.02em` | Chữ bên trong Status Badge (`COOKING`, `OCCUPIED`, `Best-seller`), nhãn phụ. |

### 1.3. Lưới & Khoảng cách (Layout Grid & 8pt Spacing System)

#### A. Quy chuẩn 8pt Spacing Scale (Figma Number Variables: `02_Spacing_Radius`)

* `space-1` \= **`4px`** *(Khoảng cách giữa Icon và Text trong Badge, hoặc giữa Tên món và Danh mục phụ)*.  
* `space-2` \= **`8px`** *(Padding trong Badge, khoảng cách giữa 2 nút nhỏ liền kề, khoảng cách giữa các dòng trong Order Ticket)*.  
* `space-3` \= **`12px`** *(Padding dọc của Input/Button, khoảng cách nội bộ trong Dish Card trên Mobile)*.  
* `space-4` \= **`16px`** *(Padding chuẩn của Card, khoảng cách lưới trên Mobile)*.  
* `space-5` \= **`20px`** *(Padding của Modal / Drawer)*.  
* `space-6` \= **`24px`** *(Gutter chuẩn trên Desktop, khoảng cách giữa các khối nội dung trong Dashboard)*.  
* `space-8` \= **`32px`** *(Khoảng cách phân tách giữa các khu vực lớn trên màn hình)*.  
* `space-12` \= **`48px`** *(Padding trên/dưới của các Section trên trang Customer Desktop)*.

#### B. Bo góc (Border Radius) & Đổ bóng (Elevation / Effect Styles)

* **Border Radius Variables:**  
  * `radius-sm` \= **`6px`** (Status Badge, Checkbox, Tag nhỏ).  
  * `radius-md` \= **`10px`** (Button, Form Input, Select Dropdown, Ô bàn nhỏ).  
  * `radius-lg` \= **`16px`** (Dish Card, Table Node, Order Ticket, KPI Card).  
  * `radius-xl` \= **`24px`** (Modal hộp thoại, Bottom Sheet giỏ hàng trên Mobile).  
  * `radius-full` \= **`9999px`** (Nút lọc Category dạng Pill, Avatar nhân viên).  
* **Elevation (Figma Effect Styles \- Dùng bóng màu nâu ấm `#1F1A17` thay vì đen `#000000`):**  
  * `Shadow/xs-Card`: `Y: 2px, Blur: 6px, Spread: 0px`, Color: `rgba(31, 26, 23, 0.05)` — Dùng cho `Dish Card` và `Table Node` mặc định.  
  * `Shadow/md-Hover`: `Y: 8px, Blur: 20px, Spread: -2px`, Color: `rgba(31, 26, 23, 0.10)` — Dùng khi Hover lên `Dish Card` hoặc `Active Order Ticket`.  
  * `Shadow/lg-Overlay`: `Y: 16px, Blur: 40px, Spread: -4px`, Color: `rgba(31, 26, 23, 0.18)` — Dùng cho Modal xác nhận thanh toán, Drawer chi tiết bàn và Sticky Bottom Cart Bar.

#### C. Thông số Column Grid trên Figma (3 Breakpoints chuẩn Bootstrap 5 & PRD)

| Thiết bị & Vai trò mục tiêu | Kích thước Frame (Figma) | Số cột (`Columns`) | Lề hai bên (`Margin`) | Khoảng cách cột (`Gutter`) | Độ rộng 1 cột (`Column Width`) | Lưu ý bố cục đặc thù theo PRD |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **Desktop** *(Customer Web, Staff All-in-One POS, Admin Portal)* | **`1440 × 1024px`** | **12 cột** (Stretch) | **`80px`** (Customer) / **`24px`** (khi có Sidebar `240px` của Admin/Staff) | **`24px`** | \~`86px` (Customer) / \~`74px` (Admin/Staff) | • **Customer:** Container tối đa `1280px` căn giữa (4 Dish Cards/hàng \= 3 cột/card).<br>• **Staff POS:** Chia 2 vùng: 7 cột trái cho *Sơ đồ bàn (Table Grid)* \+ 5 cột phải cố định cho *Hàng đợi đơn & Chi tiết bàn (Active Order Queue)*. |
| **Tablet (Ngang)** *(Staff POS tại quầy & cầm tay kiểm bàn)* | **`1024 × 768px`** | **8 cột** (Stretch) | **`24px`** | **`16px`** | \~`108px` | • Tối ưu vùng chạm tối thiểu **`44 × 44px`** cho mọi nút `Start Cooking`, `Mark Served`, `Check-in`.<br>• Chia 5 cột trái (Sơ đồ bàn 3 bàn/hàng) \+ 3 cột phải (Danh sách món cần xử lý). |
| **Mobile (Dọc)** *(Customer xem Menu, Đặt bàn, Gọi thêm món & Xem tiến độ)* | **`390 × 844px`** *(chuẩn trong dải `375px–430px` của PRD)* | **4 cột** (Stretch) | **`16px`** | **`12px`** | \~`80.5px` | • Hiển thị 2 Dish Cards/hàng (mỗi card 2 cột) hoặc 1 Dish Card ngang full 4 cột.<br>• Luôn chừa `Padding Bottom: 88px` để không bị che bởi **Sticky Cart Bar** và **Bottom Navigation Bar**. |

## CHẶNG 2: THƯ VIỆN THÀNH PHẦN GIAO DIỆN (CORE UI COMPONENTS & FIGMA VARIANTS)

Tạo trang riêng trên Figma tên là `📦 02_Components_Library` để xây dựng các thành phần tái sử dụng bằng **Auto Layout** và **Component Properties**.

### 2.1. Hệ thống Điều hướng (`Navigation`)

| Component Name | Variants / Properties | Cấu trúc Auto Layout & Thành phần bên trong |
| :---- | :---- | :---- |
| **`Nav/Customer-Topbar`** *(Desktop `1440px` & Mobile Header `390px`)* | • `Device`: `Desktop` \| `Mobile`<br>• `State`: `Guest` \| `Logged_In` \| `Active_DineIn` | • `Horizontal`, `Space-between`, `Padding: 16px 80px` (Desktop) / `12px 16px` (Mobile), `Fill: #FFFFFF`, `Border-bottom: 1px #E6DFD5`.<br>• **Trái:** Logo \+ Tên quán (`Playfair Display 22px #C84B31`). Khi `Active_DineIn`: hiện thêm Badge `🟢 Bàn T-04`.<br>• **Giữa (Desktop):** Links `Thực đơn` • `Đặt bàn` • `Đơn tại bàn` • `Lịch sử`.<br>• **Phải:** Nút `Giỏ đặt trước (Counter Badge)` \+ Nút `Đăng nhập/Đăng ký` (hoặc Avatar User). |
| **`Nav/Customer-BottomBar`** *(Mobile `390px` Fixed Bottom)* | • `Active_Tab`: `Menu` \| `Booking` \| `ActiveOrder` \| `Profile` | • `Horizontal`, `Height: 64px`, `Padding: 8px 16px`, `Fill: #FFFFFF`, `Shadow/lg-Overlay`.<br>• 4 Tab (`Fill Container`, `Vertical`, `Gap: 4px`, `Align: Center`). Tab đang chọn đổi icon & chữ sang `#C84B31`, nền Pill `#FBECE8`. Tab `Đơn tại bàn` có chấm đỏ khi món chuyển sang `SERVED`. |
| **`Nav/Staff-POS-Topbar`** *(Tablet `1024px` & Desktop `1440px`)* | • `Filter_Zone`: `All` \| `Zone_A` \| `Zone_B` | • `Horizontal`, `Height: 64px`, `Fill: #1F1A17` (Nền tối Espresso phân biệt rõ với Customer).<br>• **Trái:** Badge `STAFF POS` \+ Tabs lọc khu vực bàn.<br>• **Giữa:** 4 Chip đếm nhanh: `🟢 Trống (8)` \| `🟡 Đã đặt (3)` \| `🔴 Đang ngồi (4)` \| `🟣 Chờ thanh toán (1)`.<br>• **Phải:** Đồng hồ `Làm mới: 08s` \+ Nút `🔄 Refresh Now` \+ Nút `⚡ Báo hết món nhanh`. |
| **`Nav/Admin-Sidebar`** *(Desktop `240px` / Tablet `72px`)* | • `Collapsed`: `False` \| `True`<br>• `Active_Menu`: `Reports` \| `Menu` \| `Tables` \| `Staff` | • `Vertical`, `Height: Fill 1024px`, `Padding: 24px 16px`, `Gap: 8px`, `Fill: #F3EDE4`.<br>• 4 mục điều hướng chuẩn PRD: `📊 Thống kê Doanh thu` • `🍲 Quản lý Thực đơn` • `🪑 Sơ đồ Bàn` • `👥 Tài khoản Nhân viên`. |

### 2.2. Thẻ Món Ăn (`Component: Dish_Card`)

* **Properties cần tạo trên Figma:**  
  * `Layout` (Variant): `Vertical_Grid` (Desktop 4 cột / Mobile 2 cột) \| `Horizontal_Compact` (Mobile 1 cột / Staff gọi món nhanh).  
  * `Stock_State` (Variant): `Available` (`is_available = true`) \| `Out_Of_Stock` (`is_available = false`).  
  * `In_Cart` (Boolean): `False` (Hiện nút `+ Thêm`) \| `True` (Hiện bộ đếm `[-] 2 [+]`).  
  * `Best_Seller` (Boolean): `True` \| `False` (Bật/tắt nhãn `🔥 Bán chạy` góc trên ảnh).  
* **Cấu trúc Auto Layout (`Radius: 16px`, `Fill: #FFFFFF`, `Border: 1px #E6DFD5`, `Shadow/xs-Card`):**  
  1. **Khung Ảnh (`Height: 170px` Desktop / `130px` Mobile):** Gắn nhãn `🔥 Bán chạy` (`Fill: #D97706`, `Top-Left: 12px`). Khi `Stock_State = Out_Of_Stock`, bật lớp phủ đen mờ `rgba(31,26,23,0.55)` với nhãn `TẠM HẾT MÓN` giữa ảnh.  
  2. **Khung Nội dung (`Vertical`, `Padding: 14px`, `Gap: 6px`):**  
     * Nhãn danh mục (`12px #8C827A`) \+ Tên món (`16px SemiBold #1F1A17`, giới hạn 2 dòng).  
     * **Hàng Đáy (`Horizontal`, `Space-between`, `Align: Center`):** Giá tiền (`185.000đ` \- `18px Bold #C84B31`) \+ Cụm nút hành động (`+ Thêm` màu `#C84B31`, hoặc Stepper `[-] 2 [+]`, hoặc nút xám `Hết hàng` bị khóa khi `Out_Of_Stock`).

### 2.3. Ô Bàn Trên Sơ Đồ (`Component: Table_Map_Node`)

Component trung tâm của màn hình `/staff/dashboard`, giúp nhân viên nắm bắt tức thì tình trạng từng bàn ăn.

* **Properties cần tạo:**  
  * `Status` (Variant \- 5 trạng thái khớp PRD):  
    1. `AVAILABLE` (Nền `#ECFDF5`, Viền `#6EE7B7`, Chữ `#065F46`)  
    2. `RESERVED` (Nền `#FFFBEB`, Viền `#FCD34D`, Chữ `#92400E`)  
    3. `OCCUPIED` (Nền `#FBECE8`, Viền `#F5A391`, Chữ `#9E2A1B`)  
    4. `PAYMENT_REQUESTED` (Nền `#F5F3FF`, Viền `2px #5B21B6`, Chữ `#5B21B6` \+ Icon chuông rung)  
    5. `MAINTENANCE` (Nền `#F3F2F0`, Viền nét đứt `#D6D3CD`, Chữ `#57534E`, `Opacity: 65%`)  
  * `Is_Selected` (Boolean): `True` (Thêm viền ngoài `2px #1F1A17` khi đang mở chi tiết ở cột phải) \| `False`.  
* **Cấu trúc Auto Layout (`Vertical`, `Min-Width: 156px`, `Height: 132px`, `Padding: 14px`, `Radius: 16px`, `Space-between`):**  
  * **Top Row:** Mã bàn `T-01` (`18px Bold`) \+ Chip sức chứa `👥 4 ghế` (`12px`, nền trắng mờ).  
  * **Middle Row:** Badge trạng thái (`Trống` / `Khách đặt 19:00` / `Đang ăn` / `💸 Gọi thanh toán!`).  
  * **Bottom Row (Metadata động):**  
    * Nếu `AVAILABLE`: Hiện chữ `Sẵn sàng đón khách`.  
    * Nếu `RESERVED`: Hiện `KH: Nguyễn An • 19:00`.  
    * Nếu `OCCUPIED` / `PAYMENT_REQUESTED`: Hiện `⏱ 45 phút` \+ Tổng tạm tính `640.000đ` \+ Badge phụ `2 món chờ nấu`.

### 2.4. Thẻ Đơn Hàng, Badge Trạng Thái, Modal & Form Controls

2. **`Badge/Status_Pill` (`Horizontal Auto Layout`, `Padding: 4px 10px`, `Radius: 6px`, `Gap: 6px`):**  
   * `PENDING` / `RES_PENDING`: Chấm tròn `#9A3412` \+ Chữ `Chờ xác nhận / Chờ chế biến` (Nền `#FFF7ED`).  
   * `COOKING`: Icon lửa `#1E40AF` \+ Chữ `Đang chế biến` (Nền `#EFF6FF`).  
   * `SERVED` / `RES_CONFIRMED` / `PAID`: Icon check `#065F46` \+ Chữ `Đã phục vụ / Hoàn tất` (Nền `#ECFDF5`).  
   * `CANCELLED` / `RES_REJECTED`: Icon X `#991B1B` \+ Chữ `Đã hủy` (Nền `#FEF2F2`).  
3. **`Order_Item_Row` (Dòng món ăn trong Khay xử lý đơn của Staff & Màn hình theo dõi của Customer):**  
   * **Variants (`Role_View`):** `Customer_View` (Chỉ xem trạng thái \+ Nút `Hủy món` nếu còn `PENDING`) \| `Staff_View` (Có nút chuyển trạng thái tuần tự theo `US-STAFF-02`).  
   * **Hành vi nút bấm trên `Staff_View`:**  
     * Khi món đang `PENDING` \-\> Hiện nút `[🔥 Bắt đầu nấu (Start Cooking)]` \+ nút icon `[✕ Hủy/Hết món]`.  
     * Khi món đang `COOKING` \-\> Hiện nút `[✅ Đã lên món (Mark Served)]`.  
     * Khi món đã `SERVED` \-\> Hiện nhãn tĩnh `✓ Đã phục vụ lúc 19:25`.  
4. **`Modal/Confirmation_Dialog` (`Width: 480px` Desktop / `358px` Mobile, `Padding: 24px`, `Radius: 24px`, `Fill: #FFFFFF`, `Shadow/lg-Overlay`):**  
   * Dùng cho 3 kịch bản trọng tâm trong PRD:  
     1. **Gán bàn & Duyệt đặt chỗ (`US-STAFF-01`):** Chọn bàn trống từ Dropdown (`Capacity >= Guest Count`) \-\> Bấm `Xác nhận giữ bàn`.  
     2. **Cảnh báo còn món chưa lên khi gọi tính tiền (`US-CUST-04`):** Hiển thị danh sách món còn `PENDING`/`COOKING` và nhắc khách báo nhân viên hủy hoặc chờ lên đủ món.  
     3. **Xác nhận Thu ngân & Giải phóng bàn (`US-STAFF-03`):** Chọn phương thức (`Tiền mặt` / `Chuyển khoản QR tĩnh`), hiển thị Tổng thanh toán, nút `Xác nhận Đã thu tiền & In hóa đơn`.  
2. **Bộ `Button` & `Form_Input` (5 trạng thái tương tác):**  
   * **Button Variants:**  
     * `Type`: `Primary` (`#C84B31`), `Secondary-Outline` (Viền `#C84B31`, chữ `#C84B31`), `Success-Action` (`#065F46` \- dùng cho *Check-in* & *Mark Served*), `Danger` (`#991B1B` \- dùng cho *Hủy đơn/Từ chối*), `Ghost`.  
     * `State`: `Default` \| `Hover` (Tối hơn 10% \+ `Shadow/md-Hover`) \| `Active/Pressed` (`Scale: 98%`) \| `Disabled` (Nền `#E6DFD5`, chữ `#8C827A`) \| `Loading` (Thay icon trái bằng vòng xoay Spinner \+ chữ `Đang xử lý...` để chống double-click theo `US-STAFF-03`).  
   * **Form Input (`Height: 44px`, `Padding: 10px 14px`, `Radius: 10px`):**  
     * `State`: `Default` (Viền `#E6DFD5`, nền `#FFFFFF`) \| `Focus` (Viền `1.5px #C84B31` \+ Ring `4px #FBECE8`) \| `Error` (Viền `#991B1B` \+ dòng chữ đỏ `12px` báo lỗi bên dưới theo chuẩn Bean Validation) \| `Disabled` (Nền `#F3EDE4`).

## CHẶNG 3: THIẾT KẾ GIAO DIỆN CHI TIẾT (HIGH-FIDELITY UI SCREEN-BY-SCREEN SPECS)

### 3.1. Phân hệ 1: Khách hàng (`ROLE_CUSTOMER` — Tối ưu Mobile-First `390px` & Desktop `1440px`)

#### Màn hình C1: Trang Thực đơn Công khai & Chọn món (`/menu`)

* **Cấu trúc khu vực:**  
  1. **Header (`Nav/Customer-Topbar`):** Cố định phía trên. Nếu khách đang có phiên ngồi tại bàn (`Table OCCUPIED`), hiển thị thanh thông báo ngay dưới Header: `🟢 Bạn đang dùng bữa tại Bàn T-04 • Mọi món chọn thêm sẽ tự động gộp vào hóa đơn bàn.`  
  2. **Hero & Filter Bar (Sticky khi cuộn):** Thanh tìm kiếm từ khóa (`Tìm tên món ăn, nguyên liệu...`) \+ Dãy nút Pill lọc danh mục (`Tất cả`, `Khai vị`, `Món chính`, `Lẩu & Nướng`, `Tráng miệng`, `Đồ uống`) \+ Toggle `Chỉ hiện món còn hàng`.  
  3. **Main Content (Danh sách `Dish_Card`):** Hiển thị các thẻ món ăn kèm nhãn `🔥 Bán chạy` hoặc lớp phủ `TẠM HẾT MÓN` (`is_available = false`).  
  4. **Sticky Cart Drawer / Bottom Bar:** Hiển thị tổng số món đã chọn và tổng tiền tạm tính.  
* **Khác biệt bố cục Desktop (`1440px`) vs. Mobile (`390px`):**  
  * **Desktop:** Lưới **4 cột** (`3 Dish Cards / hàng` \= 9 cột trái \+ **1 Sidebar Giỏ món cố định bên phải** rộng 3 cột `380px`). Khách bấm `+ Thêm` sẽ thấy món cập nhật ngay vào cột phải mà không cần chuyển trang.  
  * **Mobile:** Lưới **2 cột** (`Vertical_Grid`) cho nhóm món chính có ảnh lớn, và **1 cột ngang** (`Horizontal_Compact`) cho nhóm Đồ uống/Món thêm. Giỏ hàng thu gọn thành **Floating Bottom Bar** (`Height: 56px`, nằm ngay trên Bottom Navigation Bar): `[🛒 3 món đặt trước • 425.000đ] [Tiếp tục Đặt bàn →]`.  
* **Chi tiết dữ liệu & Microcopy:**  
  * **Empty State (Khi tìm kiếm không ra kết quả):** Icon đĩa trống \+ *"Không tìm thấy món ăn phù hợp với từ khóa của bạn"* \+ Nút Secondary `[Xóa bộ lọc]`.

#### Màn hình C2: Đặt bàn trực tuyến & Giỏ món đặt trước (`/customer/reservations/new`)

* **Cấu trúc khu vực:**  
  1. **Khối Form thông tin đặt bàn (Trái \- 7 cột Desktop / Khối trên Mobile):**  
     * Chọn **Ngày dùng bữa** (Date Picker — khóa các ngày quá khứ).  
     * Chọn **Khung giờ đến** (Grid các nút Pill cách nhau 30 phút từ `10:00` đến `21:30`, khóa các khung giờ cách hiện tại `< 1 tiếng` theo `US-CUST-02`: `18:00`, `18:30`, `19:00`, `19:30`...).  
     * Chọn **Số lượng khách** (Stepper hoặc Pill chọn nhanh `2 người`, `4 người`, `6 người`, `8+ người`, tối đa `12`).  
     * Ô nhập **Ghi chú đặc biệt (`Special Notes`)**: Placeholder *"Ví dụ: Cần ghế trẻ em, bàn gần cửa sổ, dị ứng hải sản..."*.  
  2. **Khối Tóm tắt Giỏ món Đặt trước (Phải \- 5 cột Desktop / Khối dưới Mobile):**  
     * Cho phép khách chọn 1 trong 2 chế độ bằng Radio Card: **(A) Đặt bàn kèm gọi món trước (Tiết kiệm thời gian chờ)** hoặc **(B) Chỉ đặt giữ bàn trống (Gọi món khi đến quán)**.  
     * Danh sách các món đã chọn từ `/menu` (cho phép tăng/giảm số lượng hoặc xóa món). Nếu có món vừa chuyển sang `Out of Stock` ngay trước lúc bấm gửi, tô viền đỏ `#991B1B` quanh dòng món đó kèm thông báo: *"Món này vừa hết hàng, vui lòng xóa khỏi giỏ để tiếp tục."*  
* **CTA & Phân cấp thị giác:** Nút Primary lớn full-width ở đáy cột phải: `[Xác nhận Đặt bàn & Gửi thực đơn]` (hoặc `[Xác nhận Đặt bàn]` nếu giỏ trống).

#### Màn hình C3: Theo dõi Đơn tại bàn & Gọi thêm món (`/customer/active-order`)

* **Cấu trúc khu vực:**  
  1. **Table Status Banner (Đầu trang):** Thẻ màu Terracotta ấm `#FBECE8` hiển thị: `Bàn T-04 (Tầng 1 - 4 ghế)` • `Mã đơn: #ORD-1024` • `Giờ vào bàn: 18:45` • Nhãn phụ: `🔄 Tự động cập nhật mỗi 10s` kèm nút nhỏ `[Làm mới]`.  
  2. **Tiến trình tổng quan (Progress Summary):** Thanh 3 bước trực quan: `Chờ bếp nhận (1)` ➔ `Đang chế biến (2)` ➔ `Đã lên món (3)`.  
  3. **Danh sách Món đã gọi (`Order_Item_Row`):** Chia nhóm theo lượt gọi (*Món đặt trước* vs. *Gọi thêm lúc 19:10*).  
     * Món đang `PENDING`: Badge cam `Chờ chế biến` \+ Nút Ghost đỏ `[✕ Hủy món]` (chỉ mở khi còn `PENDING` theo `US-CUST-03`).  
     * Món đang `COOKING`: Badge xanh lam `🔥 Bếp đang nấu` (Nút hủy bị ẩn/khóa).  
     * Món đã `SERVED`: Badge xanh ngọc `✓ Đã phục vụ`.  
  4. **Sticky Action Footer (2 nút song song):**  
     * Nút Secondary Outline (Trái): `[+ Gọi thêm món]` (Chuyển về `/menu` ở chế độ gắn thêm vào `Order_ID` hiện tại).  
     * Nút Primary (Phải): `[💸 Yêu cầu Thanh toán (Request Bill)]`.  
* **Xử lý Edge Case theo PRD (`US-CUST-04`):** Nếu khách bấm `[Yêu cầu Thanh toán]` khi vẫn còn món ở trạng thái `PENDING` hoặc `COOKING`, bật **Modal Cảnh báo**: *"Bàn của bạn vẫn còn 02 món đang chờ chế biến. Vui lòng chờ món lên đủ hoặc nhờ Nhân viên hủy món chưa nấu trước khi yêu cầu hóa đơn."*

### 3.2. Phân hệ 2: Nhân viên Vận hành (`ROLE_STAFF` — Tối ưu Tablet `1024px` & Desktop `1440px`)

#### Màn hình S1: Bảng Điều khiển Vận hành Tất-cả-trong-một (`/staff/dashboard`)

Màn hình trung tâm gom toàn bộ quy trình **Duyệt đặt bàn \-\> Check-in \-\> Điều phối bếp \-\> Thu ngân** về một giao diện duy nhất.

* **Bố cục Chia 2 Phân vùng Cố định (Split-Screen POS Layout):**  
  * **Phân vùng Trái (7 cột trên Desktop `820px` / 5 cột trên Tablet `600px`): Sơ đồ Bàn Trực quan (`Table Status Grid`) \+ Hàng đợi Đặt bàn (`Reservation Queue`)**  
    1. **Thanh Tab Chuyển đổi Nhanh trên đầu vùng trái:**  
       * Tab 1: `🪑 Sơ đồ Bàn (16)` — Hiển thị lưới các thẻ `Table_Map_Node` (4 bàn/hàng trên Desktop, 3 bàn/hàng trên Tablet). Các bàn đang `PAYMENT_REQUESTED` có viền tím `#5B21B6` nổi bật.  
       * Tab 2: `📅 Đơn Đặt bàn Chờ duyệt (3 RES_PENDING)` & `Sắp đến (2 RES_CONFIRMED)` — Hiển thị danh sách thẻ đặt bàn cần xử lý.  
  * **Phân vùng Phải (5 cột trên Desktop `560px` / 3 cột trên Tablet `400px` \- Cố định chiều cao 100vh): Khay Chi tiết & Thao tác Nhanh (`Contextual Action Inspector`)** Nội dung cột phải tự động thay đổi theo đối tượng mà Staff vừa chạm/click ở cột trái:  
    * **Trạng thái A — Khi chọn một Đơn đặt bàn `RES_PENDING`:**  
      * Hiển thị: Tên khách, SĐT, Ngày giờ đến, Số khách (`6 người`), Ghi chú đặc biệt, và Danh sách món đặt trước.  
      * **Bộ chọn gán bàn (`Assign Physical Table`):** Dropdown hoặc lưới chip chỉ lọc các bàn đang `AVAILABLE` trong khung giờ đó và có `Sức chứa >= 6 ghế` (VD: `Bàn T-08 (8 ghế)`).  
      * **2 nút CTA lớn:** `[✓ Duyệt & Gán bàn]` (`#065F46`) và `[✕ Từ chối đặt bàn]` (`#991B1B` — mở ô nhập lý do từ chối).  
    * **Trạng thái B — Khi chọn Bàn `RESERVED` (Khách vừa tới quán):**  
      * Hiển thị thông tin phiếu đặt chỗ đã duyệt \+ Nút CTA chính cỡ lớn (`Height: 52px`): `[👋 Khách đến - Check-in & Mở bàn]`. Ngay khi bấm, bàn chuyển sang `OCCUPIED` và toàn bộ món đặt trước tự động nạp vào danh sách món của bàn.  
    * **Trạng thái C — Khi chọn Bàn `OCCUPIED` (Điều phối món ăn tại bàn):**  
      * **Header Khay phải:** `Bàn T-04 • Khách: Trần Minh • Đang ngồi 45p` \+ Nút `[+ Thêm món cho bàn]`.  
      * **Danh sách món theo thứ tự ưu tiên xử lý (`Order_Item_Row`):**  
        * Nhóm 1 (`PENDING` \- Nền cam nhạt): Nút bấm 1 chạm `[🔥 Bắt đầu nấu]` \+ Nút phụ `[Hết món/Hủy]`.  
        * Nhóm 2 (`COOKING` \- Nền xanh lam nhạt): Nút bấm 1 chạm `[✅ Đã lên món]`.  
        * Nhóm 3 (`SERVED` \- Nền xanh lá nhạt): Thu gọn phía dưới kèm đơn giá × số lượng.  
      * **Footer Khay phải:** Tổng tiền tạm tính (`640.000đ` \- chỉ cộng các món không bị hủy) \+ Nút `[💳 Mở Màn hình Thu ngân]`.

#### Màn hình S2: Modal Thu ngân Tại quầy & Hóa đơn In (`/staff/orders/{id}/invoice`)

* **Modal Xác nhận Thanh toán (`Counter Checkout Modal`):**  
  * Khi bàn ở trạng thái `PAYMENT_REQUESTED` và Staff bấm `[💳 Thu ngân & Thanh toán]`:  
  * Hiển thị bảng đối soát từng món `SERVED` (`Tên món`, `SL`, `Đơn giá lúc đặt`, `Thành tiền`), tổng cộng tiền thanh toán (`Grand Total`).  
  * **Chọn Phương thức Thanh toán (2 Radio Cards lớn):** `[💵 Tiền mặt (CASH)]` hoặc `[🏦 Chuyển khoản Ngân hàng (BANK_TRANSFER - Hiện mã QR tĩnh của quán)]`.  
  * **Nút CTA Khóa sổ (`@Transactional`):** `[✓ Xác nhận Đã thu tiền & Giải phóng bàn]`.  
* **Trang Hóa đơn In (`Printable JSP Invoice` \- Mở ngay sau khi xác nhận `PAID`):**  
  * Giao diện khổ giấy nhiệt `80mm` (hoặc A5 căn giữa màn hình), nền trắng, chữ đen sắc nét, tự động gọi `window.print()` và có nút `[🖨️ In hóa đơn]` \+ `[← Quay lại Sơ đồ bàn]`.

### 3.3. Phân hệ 3: Quản trị viên (`ROLE_ADMIN` — Tối ưu Desktop `1440px`)

#### Màn hình A1: Dashboard Thống kê Doanh thu & Hiệu suất (`/admin/reports`)

* **Cấu trúc khu vực (12 cột bên phải Sidebar `240px`):**  
  1. **Top Filter Header:** Tiêu đề `Báo cáo Doanh thu & Vận hành` \+ Bộ lọc khoảng ngày (`Từ ngày` \- `Đến ngày`, kèm các nút chọn nhanh: `Hôm nay`, `7 ngày qua`, `Tháng này`) \+ Nút `[Áp dụng bộ lọc]`.  
  2. **Hàng 4 Thẻ KPI (`KPI Summary Cards` \- mỗi thẻ 3 cột, `Height: 120px`):**  
     * **Card 1 (Nền nhấn `#FBECE8`, viền `#C84B31`):** `Tổng Doanh thu Thực thu` (Chỉ tính đơn `PAID`) — Số lớn `42.850.000đ`.  
     * **Card 2:** `Tổng Đơn Hoàn thành (PAID)` — `128 đơn`.  
     * **Card 3:** `Lượt Đặt bàn Hủy / Từ chối` — `9 lượt (6.5%)`.  
     * **Card 4:** `Giá trị Trung bình / Bàn (AOV)` — `334.700đ`.  
  3. **Hàng Biểu đồ (`Chart.js` Canvas \- Chia 8 cột trái \+ 4 cột phải):**  
     * **Khối Trái (8 cột):** Biểu đồ Cột kết hợp Đường (`Bar/Line Chart`) thể hiện **Doanh thu theo từng ngày trong khoảng lọc** (màu cột `#C84B31`). *(Nếu khoảng ngày không có đơn `PAID`, hiển thị Empty State giữa khung biểu đồ: `"Chưa có dữ liệu doanh thu trong khoảng thời gian đã chọn"` theo `US-ADMIN-03`)*.  
     * **Khối Phải (4 cột):** Bảng xếp hạng **Top 5 Món Bán Chạy Nhất** (Hiển thị Ảnh thumbnail 40x40px, Tên món, Thanh ngang tỷ lệ, Số phần đã bán và Tổng doanh thu mang lại).

#### Màn hình A2: Quản lý Thực đơn, Bàn ăn & Nhân sự (`/admin/menu`, `/admin/tables`, `/admin/staff`)

* **Màn hình Quản lý Thực đơn (`/admin/menu`):**  
  * **Thanh công cụ:** Tabs lọc theo Danh mục \+ Ô tìm kiếm \+ Nút `[+ Quản lý Danh mục]` \+ Nút Primary `[+ Thêm món ăn mới]`.  
  * **Bảng dữ liệu (`Data Table`):** Cột `Ảnh (56x56px, bo góc 8px)` \| `Tên món & Mô tả ngắn` \| `Danh mục` \| `Đơn giá` \| `Trạng thái Mở bán (Toggle Switch gạt trực tiếp Bật/Tắt)` \| `Thao tác (Sửa / Ẩn món - Soft Delete)`.  
  * **Adaptation khi xem trên màn hình nhỏ/Tablet:** Ẩn cột `Mô tả ngắn`, giữ nguyên nút gạt `Toggle Switch` để Admin/Staff có thể khóa món hết nguyên liệu chỉ bằng 1 chạm.  
* **Màn hình Quản lý Sơ đồ Bàn (`/admin/tables`) & Nhân sự (`/admin/staff`):**  
  * **Quản lý Bàn (`/admin/tables`):** Bảng \+ Chế độ xem Thẻ hiển thị `Mã bàn (T-01)`, `Khu vực (Zone)`, `Sức chứa (2/4/6/8 ghế)`, `Trạng thái hiện tại`. *(Theo Edge Case `US-ADMIN-02`: Nếu bàn đang `OCCUPIED` hoặc đã gán cho lịch `RES_CONFIRMED`, nút `Chuyển sang Bảo trì` và `Xóa` bị làm mờ Disabled kèm Tooltip: `"Không thể bảo trì/xóa bàn đang có khách hoặc có lịch đặt sắp tới"`).*  
  * **Quản lý Nhân sự (`/admin/staff`):** Bảng danh sách tài khoản `ROLE_STAFF` (`Họ tên`, `Email/Username`, `Số điện thoại`, `Ngày tạo`, Badge trạng thái `Đang hoạt động` / `Đã khóa LOCKED`) \+ Nút `[🔒 Khóa tài khoản]` hoặc `[🔓 Mở khóa]` \+ Nút `[+ Tạo tài khoản Nhân viên]`.

## CHẶNG 4: KỊCH BẢN BẢN MẪU TƯƠNG TÁC (INTERACTIVE PROTOTYPE & USER FLOWS TRÊN FIGMA)

Tạo **3 Flow Starting Points** trong tab **Prototype** của Figma tương ứng với 3 vai trò (`Flow 1: Customer Mobile/Desktop`, `Flow 2: Staff All-in-One POS`, `Flow 3: Admin Back-office`).

### 4.1. Luồng 1 — Khách hàng (`ROLE_CUSTOMER`): Xem thực đơn ➔ Đặt bàn & Chọn món trước ➔ Gọi thêm tại bàn & Theo dõi trạng thái

| Bước | Màn hình Nguồn (From) | Phần tử Kích hoạt (Hotspot) | Trigger | Action & Màn hình Đích (To) | Animation & Thời gian | Phản hồi Giao diện (UI Feedback) |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **1.1** | `C1_Menu_Default` | Nút Pill danh mục `Món chính` | **On Click** | **Change to** (Variant `Category = Main_Course` hoặc Frame `C1_Menu_Filtered`) | **Smart Animate** • `Ease Out` • `200ms` | Pill `Món chính` đổi sang nền `#C84B31` (chữ trắng), lưới món ăn trượt nhẹ cập nhật danh sách. |
| **1.2** | `C1_Menu_Filtered` | Nút `[+ Thêm]` trên thẻ *Bò Bít Tết Sốt Tiêu Đen* | **On Click** | **Change to** Variant `In_Cart = True` (`[-] 1 [+]`) \+ Hiển thị `Sticky_Cart_Bar` ở đáy | **Smart Animate** • `Ease Out` • `250ms` | Thanh `Sticky_Cart_Bar` trượt từ dưới đáy lên (**Move In** `↑`), nhảy số `1 món • 185.000đ`. |
| **1.3** | `C1_Menu_WithCart` | Nút `[Tiếp tục Đặt bàn →]` trên thanh Sticky Cart | **On Click** | **Navigate to** `C2_Reservation_Form` | **Move In** (Direction: `Left`) • `Ease Out` • `300ms` | Chuyển sang trang điền Ngày, Giờ, Số khách, có sẵn danh sách món đặt trước. |
| **1.4** | `C2_Reservation_Form` | Chọn Pill `19:00`, `4 khách` rồi bấm `[Xác nhận Đặt bàn & Gửi thực đơn]` | **On Click** | **Open Overlay** `Toast_Booking_Submitted` \-\> **After Delay (`1200ms`)** \-\> **Navigate to** `C2B_Reservation_Waiting` | **Dissolve** • `200ms` | Nút đổi sang trạng thái `Loading (Spinner)`, hiện Toast xanh `#065F46`: *"Đã gửi yêu cầu đặt bàn \#RES-208\!"*, hiển thị Badge `🟡 RES_PENDING (Chờ nhà hàng xác nhận)`. |
| **1.5** | `C2B_Reservation_Waiting` | *(Mô phỏng sau khi Staff Check-in khách tại quán)* Bấm vào Banner `Vào màn hình Bàn T-04` | **On Click** | **Navigate to** `C3_Active_DineIn_Order` | **Smart Animate** • `300ms` | Hiển thị màn hình Đơn tại bàn `T-04` với các món ở trạng thái `🟠 PENDING (Chờ chế biến)` và nút `[✕ Hủy món]` đang mở. |
| **1.6** | `C3_Active_DineIn_Order` | Nút `[🔄 Làm mới]` *(hoặc tự động **After Delay `3000ms`** mô phỏng AJAX Polling 10s)* | **On Click** / **After Delay** | **Navigate to** `C3_Active_Order_Updated` | **Smart Animate** • `Ease In And Out` • `350ms` | Thanh tiến trình nhảy sang bước 2 & 3; Badge món ăn đổi màu mượt mà từ `🟠 PENDING` ➔ `🔵 COOKING` ➔ `🟢 SERVED` (ẩn nút Hủy món). |
| **1.7** | `C3_Active_Order_Updated` | Nút `[💸 Yêu cầu Thanh toán]` *(khi tất cả món đã `SERVED`)* | **On Click** | **Open Overlay** `Modal_Request_Bill_Confirm` (Centered, Background Dim `40%`) | **Dissolve** • `200ms` | Khi khách bấm `[Xác nhận gọi tính tiền]` trong Modal \-\> Đổi Banner đầu trang thành `🟣 Đã gửi yêu cầu thanh toán - Nhân viên đang chuẩn bị hóa đơn`. |

### 4.2. Luồng 2 — Nhân viên (`ROLE_STAFF`): Nhận đơn đặt bàn ➔ Gán bàn & Check-in ➔ Cập nhật tiến độ món ➔ Thu ngân & Giải phóng bàn

| Bước | Màn hình Nguồn (From) | Phần tử Kích hoạt (Hotspot) | Trigger | Action & Màn hình Đích (To) | Animation & Thời gian | Phản hồi Giao diện (UI Feedback) |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **2.1** | `S1_Dashboard_Queue` | Thẻ đặt bàn mới `#RES-208 (4 khách - 19:00)` ở cột trái | **On Click** | **Navigate to** `S1_Inspector_AssignTable` | **Smart Animate** • `200ms` | Khay bên phải (Inspector) nạp chi tiết đơn `#RES-208` và lọc danh sách các bàn trống `>= 4 ghế`. |
| **2.2** | `S1_Inspector_AssignTable` | Chọn chip `Bàn T-04 (4 ghế)` \+ Bấm `[✓ Duyệt & Gán bàn]` | **On Click** | **Navigate to** `S1_Dashboard_TableReserved` | **Smart Animate** • `300ms` | Ô `Bàn T-04` trên sơ đồ đổi từ Xanh lá (`AVAILABLE`) sang Vàng Hổ phách (`RESERVED - 19:00`). Hiện Toast góc trên phải: *"Đã gán Bàn T-04 cho KH Nguyễn An"*. |
| **2.3** | `S1_Dashboard_TableReserved` | Bấm vào ô `Bàn T-04 (RESERVED)` \-\> Bấm nút lớn `[👋 Khách đến - Check-in & Mở bàn]` ở cột phải | **On Click** | **Navigate to** `S1_Dashboard_TableOccupied` | **Smart Animate** • `350ms` | Ô `Bàn T-04` đổi sang Đỏ Terracotta (`OCCUPIED`), bộ đếm giờ bắt đầu chạy `⏱ 01 phút`, các món đặt trước tự động đổ vào danh sách `PENDING` ở cột phải. |
| **2.4** | `S1_Dashboard_TableOccupied` | Nút `[🔥 Bắt đầu nấu]` rồi bấm tiếp `[✅ Đã lên món]` trên từng dòng `Order_Item_Row` | **On Click** | **Change to** Variant `Item_Status = COOKING` ➔ `Item_Status = SERVED` | **Smart Animate** • `200ms` | Dòng món ăn đổi màu nền từ Cam nhạt ➔ Xanh lam nhạt ➔ Xanh lá nhạt và trượt xuống nhóm "Đã phục vụ" bên dưới. |
| **2.5** | `S1_Dashboard_PaymentAlert` | Ô `Bàn T-04` nhấp nháy Tím (`PAYMENT_REQUESTED`) \-\> Bấm nút `[💳 Thu ngân & Thanh toán]` | **On Click** | **Open Overlay** `S2_Modal_Counter_Checkout` (Centered, Background `#1F1A17` `50%`) | **Move In** (`↑`) • `250ms` | Mở hộp thoại đối soát hóa đơn, cho phép chọn `💵 Tiền mặt` hoặc `🏦 Chuyển khoản (Hiện QR tĩnh)`. |
| **2.6** | `S2_Modal_Counter_Checkout` | Nút `[✓ Xác nhận Đã thu tiền & Giải phóng bàn]` | **On Click** | **Navigate to** `S2B_Printable_Invoice_View` \-\> Bấm `[← Về Sơ đồ bàn]` \-\> `S1_Dashboard_TableReleased` | **Dissolve** • `250ms` | Mở bản xem trước Hóa đơn in khổ 80mm (`PAID`). Khi quay lại Dashboard, `Bàn T-04` đã tự động trở về màu Xanh lá (`AVAILABLE`). |

### 4.3. Luồng 3 — Quản trị viên (`ROLE_ADMIN`): Bật/tắt "Hết hàng" & Thêm món mới ➔ Lọc biểu đồ doanh thu

| Bước | Màn hình Nguồn (From) | Phần tử Kích hoạt (Hotspot) | Trigger | Action & Màn hình Đích (To) | Animation & Thời gian | Phản hồi Giao diện (UI Feedback) |
| :---- | :---- | :---- | :---- | :---- | :---- | :---- |
| **3.1** | `A2_Admin_Menu_List` | Nút gạt `Toggle Switch` ở cột *Trạng thái Mở bán* của món *Cá Hồi Áp Chảo* | **On Click** | **Change to** Variant `Stock_Toggle = Off` \+ **Open Overlay** `Toast_Stock_Updated` | **Smart Animate** • `180ms` | Nút gạt trượt sang trái (màu xám `#8C827A`), Badge đổi thành `Hết hàng (Out of Stock)`. Hiện Toast: *"Đã khóa bán món Cá Hồi Áp Chảo trên Thực đơn"*. |
| **3.2** | `A2_Admin_Menu_List` | Nút Primary `[+ Thêm món ăn mới]` góc trên phải | **On Click** | **Open Overlay** `A2_Modal_Dish_Form` (Hoặc Drawer trượt từ cạnh phải vào) | **Move In** (`Left`) • `250ms` | Mở form nhập Tên món, Danh mục, Giá bán (`> 0`), URL ảnh, Mô tả. Khi bấm `[Lưu món ăn]`, đóng Drawer và thêm dòng mới vào đầu bảng. |
| **3.3** | `A2_Admin_Menu_List` | Mục `📊 Thống kê Doanh thu` trên `Nav/Admin-Sidebar` bên trái | **On Click** | **Navigate to** `A1_Admin_Revenue_Dashboard` | **Dissolve** • `200ms` | Chuyển sang màn hình Báo cáo Doanh thu với 4 thẻ KPI và biểu đồ `Chart.js`. |
| **3.4** | `A1_Admin_Revenue_Dashboard` | Chọn bộ lọc `Tháng này` \+ Bấm `[Áp dụng bộ lọc]` | **On Click** | **Navigate to** `A1_Dashboard_Skeleton` \-\> **After Delay (`600ms`)** \-\> `A1_Dashboard_Filtered_Data` | **Smart Animate** • `400ms` | Hiển thị hiệu ứng **Skeleton Loading** trong `0.6s`, sau đó các cột biểu đồ doanh thu cao dần lên (`Smart Animate` chiều cao cột) và cập nhật bảng **Top 5 Món Bán Chạy Nhất**. |

### 4.4. Quy chuẩn 3 Trạng thái Phản hồi (Micro-interactions & System States)

1. **Toast Notification (`Top-Right Floating`, tự đóng sau `3000ms`):**  
   * `Success` (Viền trái `4px #065F46`, nền `#ECFDF5`): Dùng khi Đặt bàn thành công, Check-in xong, Đã thu tiền.  
   * `Warning/Alert` (Viền trái `4px #5B21B6`, nền `#F5F3FF`): Dùng trên màn hình Staff khi có bàn bấm `Yêu cầu thanh toán` (`"🔔 Bàn T-04 vừa yêu cầu thanh toán hóa đơn!"`).  
   * `Error` (Viền trái `4px #991B1B`, nền `#FEF2F2`): Dùng khi món vừa hết hàng hoặc bàn vừa bị nhân viên khác gán trùng (`@Transactional` conflict).  
2. **Skeleton Loading (`Shimmers`):**  
   * Dùng các khối chữ nhật bo góc `8px` tô dải chuyển sắc ấm (`Linear Gradient: #F3EDE4 -> #FAF7F2 -> #F3EDE4`) cho lưới `Dish_Card` và biểu đồ doanh thu khi chờ dữ liệu.  
3. **Empty States (Trạng thái rỗng có nút hành động):**  
   * Giỏ đặt trước trống • Tìm kiếm thực đơn không có kết quả • Không có đơn đặt bàn chờ duyệt • Khoảng ngày thống kê có doanh thu `0đ` (hiển thị minh họa nét mảnh màu `#8C827A` \+ câu thông báo rõ ràng \+ nút CTA phụ).