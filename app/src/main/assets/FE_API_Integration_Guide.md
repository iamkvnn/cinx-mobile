# Hướng dẫn Tích hợp API Toàn Diện cho Frontend (Cinx)
Tài liệu này mô tả toàn bộ các luồng nghiệp vụ (flows) chính chạy qua các Microservices của vườm ươm Cinx, từ góc độ người dùng (ứng dụng Frontend) để dễ dàng tích hợp.

---

## 1. Luồng Xác Thực (Authentication) & Hồ Sơ (User Profile)
Quản lý việc đăng ký, đăng nhập và thông tin cá nhân.
*(Service: `auth`, `user`)*

### Quy trình (Flow):
1. **Đăng ký & Xác thực OTP**:
   - `POST /api/v1/auth/register`
   - Nhận OTP từ email, sau đó gọi `POST /api/v1/auth/verify-otp`.
   - Nếu cần có thể gọi `POST /api/v1/auth/send-otp` để gửi lại.
2. **Đăng nhập**: 
   - `POST /api/v1/auth/login`
   - FE nhận về `TokenResponseDto` (bao gồm `accessToken` và `refreshToken`).
3. **Quản lý phiên (Refresh Token)**:
   - Khi access token hết hạn, gọi `POST /api/v1/auth/refresh-token` với `refreshToken` cũ để lấy cặp token mới.
4. **Quản lý Hồ Sơ & Mật Khẩu**:
   - Lấy thông tin user hiện tại (bao gồm cả `xp`): `GET /api/v1/users/me`
   - Cập nhật hồ sơ/Avatar: `PUT /api/v1/users/{id}` (dùng `multipart/form-data`)
   - Đổi mật khẩu: `POST /api/v1/auth/send-change-password-otp` -> `POST /api/v1/auth/change-password`

---

## 2. Luồng Trải Nghiệm Khóa Học & Khám Phá (Course Discovery)
User dạo quanh hệ thống phân loại, tìm kiếm và xem khoá học.
*(Service: `course`, `social`)*

### Quy trình (Flow):
1. **Lướt & Tìm kiếm khóa học**:
   - `GET /api/v1/courses`: Trả về danh sách phân trang (kèm tìm kiếm, filter theo loại).
   - FE có thể hiển thị ảnh khoá học hoặc gọi các API từ `CategoryController` (lấy danh mục hệ thống).
2. **Xem Chi Tiết Khóa Học**:
   - `GET /api/v1/courses/{id}`: Trả về toàn bộ thông tin chi tiết (`CourseDetailResponse`), bao gồm cả rating, các module, giá tiền.
3. **Quản lý Wishlist (Danh sách yêu thích)**:
   - Thêm vào Wishlist: `POST /api/v1/wishlist`
   - Lấy danh sách Wishlist: `GET /api/v1/wishlist`
   - Bỏ quan tâm: `DELETE /api/v1/wishlist` (truyền courseId)

---

## 3. Luồng Giỏ Hàng, Thanh Toán & Ghi Danh (Cart & Checkout)
Hành trình từ khi muốn mua cho đến khi thanh toán thành công và được phép vào học.
*(Service: `cart`, `payment`, `enrollment`)*

### Quy trình (Flow):
1. **Giỏ hàng (Cart)**:
   - Thêm khoá học: `POST /api/v1/cart` (body: `itemId`)
   - Xem giỏ hàng hiện tại: `GET /api/v1/cart`
   - Xoá khỏi giỏ: `DELETE /api/v1/cart/{itemId}` (hoặc list `/ids`, hoặc xoá hết `/clear`)
2. **Khởi tạo Đơn Hàng (Order)**:
   - Chọn các khoá học trong giỏ, dùng mã giảm giá (check `GET /api/v1/vouchers`), sau đó gọi tạo đơn:
   - `POST /api/v1/orders` -> Server sinh ra hoá đơn (`orderId`, tổng tiền).
3. **Thanh Toán (Payment) - Momo / VNPay**:
   - Từ `orderId`, gọi API Payment (Ví dụ: `POST /api/v1/payments`).
   - Server gọi cổng thanh toán và trả về `paymentUrl`. FE redirect user sang link đó để quét mã/nhập thẻ.
   - Khi thanh toán xong (callback từ MoMo/VNPay báo về Backend thành công), Backend tự động ghi nhận hoá đơn đã trả.
4. **Kiểm tra quyền truy cập (Enrollment)**:
   - Gọi `POST /api/v1/enrollments/check` (truyền list `courseIds`) để đánh dấu nút UI thành "Vào Học" thay vì "Thêm vào giỏ" nếu user đã ghi danh.
   - Lấy toàn bộ khóa học đã mua: `GET /api/v1/enrollments`

---

## 4. Luồng Học Tập, Điểm Kinh Nghiệm (XP) & Mục Tiêu Hàng Ngày 
Theo dõi tiến độ học bài, thưởng XP và Gamification.
*(Service: `learning`, `user`)*

### Quy trình (Flow):
1. **Lấy mục tiêu ngày hiện tại (Daily Goal)**: 
   - `GET /api/v1/daily-goals`
   - Trả về mục tiêu hoặc `null` nếu user chưa set hôm nay. Nếu `null`, FE hiển thị popup tạo Goal.
   - Để tạo mới/cập nhật mục tiêu: `POST` hoặc `PUT /api/v1/daily-goals` (với `targetXp`).
2. **Kích hoạt bài học hoàn thành**:
   - Khi user xem xong 1 video / làm xong 1 bài, gọi: `POST /api/v1/learning/course-progress/items/{itemId}/complete`
   - **Tác động tự động:**
     - Tiến độ khoá học tăng lên.
     - Tăng tự động **+50 XP** vào tổng XP profile user (`User Service`).
     - Tự động cộng **+50 XP** vào `currentXp` của Daily Goal hôm nay. Nếu đạt chỉ tiêu, trạng thái ngầm tự động ghi nhận là `isCompleted = true`.
3. **Xem Tiến Độ**:
   - Tiến độ khóa học: `GET /api/v1/learning/course-progress/{courseId}` (Hiển thị phần trăm đã học xong).
   - Check tổng XP: `GET /api/v1/users/me`

---

## 5. Luồng Đánh Giá Khóa Học (Reviews)
Cho phép user review lại những khóa đã hoàn thành/ghi danh.
*(Service: `social`, `course`)*

### Quy trình (Flow):
1. **Viết Đánh Giá Mới**: 
   - `POST /api/v1/reviews` 
2. **Tương tác**:
   - Có thể sửa (`PUT`) hoặc xóa (`DELETE`) đánh giá của mình.
   - **Tự động**: Mỗi khi review được tạo/sửa đổi/xoá, Microservice Social sẽ báo cho Course Service để tính lại lượng sao trung bình (`Average Rating`), do đó bên Course tự động nhảy sao mà FE không cần tác động thủ công.

---

## 6. Luồng Chứng Chỉ Học Tập (Certificates)
Khi một sinh viên cày xong khoá học!
*(Service: `learning`)*

### Quy trình (Flow):
1. **Điều kiện hiển thị**: Khi tiến trình (`/api/v1/learning/course-progress/{courseId}`) đạt = 100%, hiện nút Claim Certificate.
2. **Yêu cầu & Lấy chứng chỉ**: 
   - Xin cấp phát: `POST /api/v1/certificates/apply/{courseId}`
   - Xem chứng chỉ cụ thể: `GET /api/v1/certificates/my-certificate/{courseId}`
   - Xem tất cả chứng chỉ của mình sở hữu: `GET /api/v1/certificates/my-certificates`

---

## 7. Luồng Thông Báo Đẩy (Push Notifications)
Thông báo thành tích, nhắc nhở hoặc tin nhắn hệ thống.
*(Service: `notification`, `user`)*

### Quy trình (Flow):
1. **Đăng ký Device Token**: 
   - Sau khi login, FE gọi `POST /api/v1/users/device-tokens` (chứa `deviceToken` của FCM) để Server nhớ thiết bị.
2. **Quản lý danh sách thông báo in-app**:
   - List Notification: `GET /api/v1/notifications`
   - Số đếm chuông đỏ: `GET /api/v1/notifications/unread-count`
   - Đánh dấu đã đọc: `POST /api/v1/notifications/{id}/toggle-read`
3. **Test Push FCM**:
   - (Môi trường Dev) Có thể dùng `POST /api/v1/notifications/test-push` để bắn notification test thẳng xuống thiết bị.

---