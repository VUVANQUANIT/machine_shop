# Machine Shop API – Spring Boot Backend

## Overview

Machine Shop API is a backend service for an industrial equipment e-commerce application.
It provides secure RESTful APIs for user authentication, product catalog management, and integration with an Angular frontend.

The project is built on **Spring Boot 3** and follows modern backend best practices: layered architecture, DTO mapping, centralized error handling, JWT-based security, and clear API contracts via OpenAPI/Swagger.

---

## Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.10 |
| ORM & Persistence | Spring Data JPA (Hibernate) |
| Security | Spring Security + JWT (`jjwt 0.12.3`) + BCrypt |
| Database | PostgreSQL 15 (primary), H2 (test) |
| Messaging | RabbitMQ (async email queue via Spring AMQP) |
| Caching / Token Store | Redis (OTP, reset token, TTL, rate limiting) |
| Object Mapping | MapStruct 1.5.5 + Lombok |
| API Documentation | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| Image Storage | Cloudinary HTTP client |
| Validation | Spring Boot Starter Validation (Bean Validation) |
| Environment Config | `spring-dotenv` (.env file support) |
| Build | Maven + Spring Boot Maven Plugin |
| Testing | Spring Boot Test, Spring Security Test, H2 |

---

## Main Features

### Authentication & Authorization
- User registration and login with JWT-based stateless authentication
- Access token + refresh token flow
- Role-based access control (admin vs. public endpoints)

### Password Recovery (OTP Flow)
- "Forgot password" via email OTP
- 6-digit OTP valid for **5 minutes**, stored in Redis
- Reset token valid for **10 minutes**, stored in Redis
- Maximum **5 wrong OTP attempts** before requiring a new code

### Welcome Email (Async)
- Automatically sends a welcome email after successful registration
- Processed asynchronously via **RabbitMQ** queue — does not block API response

### Product Catalog
**Public APIs (no auth required):**
- Product listing with pagination
- Full-text keyword search
- Filtering by category and price range
- Product detail: basic info or extended (images, category name, technical specifications)
- Category listing (for dropdowns / filters)

**Admin APIs (JWT required):**
- Full CRUD for products and categories
- Upload multiple product images (`multipart/form-data`)
- Manage technical specifications per product

### Standardized Error Handling
- Unified error response format: HTTP status, error code (`code`), message, request path
- Optional per-field validation errors
- Designed error codes for Angular interceptors: `TOKEN_EXPIRED`, `INVALID_TOKEN`, `INVALID_CREDENTIALS`

---

## Architecture & Design

### Layered Architecture
```
Controller  →  Service  →  Repository  →  PostgreSQL
                         ↘  Redis (OTP, reset token)
                         ↘  RabbitMQ (email queue)
                         ↘  Cloudinary (image storage)
```

### DTO-based API
- Separate DTOs for public listing, product detail, admin CRUD, and all auth flows
- **MapStruct** for entity ↔ DTO mapping, combined with **Lombok** for boilerplate reduction

### Security Design
- JWT stateless authentication (no server-side session)
- BCrypt password hashing
- All sensitive configuration via environment variables (never hardcoded)

### Angular Integration
- Consistent `ApiResponse<T>` wrapper for all admin APIs
- Clear REST contracts documented in `API.md`
- Error codes designed for Angular HTTP interceptors
- CORS support and relative image URL paths for easy frontend rendering

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (for PostgreSQL, Redis, RabbitMQ)
- Git

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd machinesshop
```

### 2. Configure environment variables

```bash
cp .env.example .env
```

Edit `.env` with your secrets:

```properties
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_secure_secret_key_at_least_64_characters
# Add Cloudinary, Redis, RabbitMQ, mail server configs as needed
```

### 3. Start services

```bash
docker-compose up -d
```

### 4. Run the application

**Option 1 – Maven Wrapper (recommended)**

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Option 2 – Scripts**

```bash
# Windows
run.bat

# Linux / macOS
chmod +x run.sh && ./run.sh
```

**Option 3 – From IDE**

Read `IDE_SETUP.md` before running from IDE (required for correct timezone config).

Set active profile via environment variable:

```
SPRING_PROFILES_ACTIVE=dev
```

Profiles:
- `dev` — Swagger enabled, debug logging
- `prod` — Swagger disabled, optimized

### 5. Access Points

| Endpoint | URL |
|---|---|
| API base | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/api/health` |

---

## API Overview

### Auth Endpoints

| Method | Path | Description |
|---|---|---|
| POST | `/api/auth/login` | Login, receive access + refresh token |
| POST | `/api/auth/register` | Register (sends welcome email async) |
| POST | `/api/auth/refresh` | Refresh access token |
| POST | `/api/auth/forgot-password/request` | Request OTP via email |
| POST | `/api/auth/forgot-password/verify-otp` | Verify OTP, receive reset token |
| POST | `/api/auth/forgot-password/reset` | Reset password using reset token |

### Public Endpoints (no auth)

| Method | Path | Description |
|---|---|---|
| GET | `/api/health` | Health check |
| GET | `/api/public/products` | Product listing |
| GET | `/api/public/products/{id}` | Product detail (basic) |
| GET | `/api/public/products/detail/{id}` | Product detail (images, specs, category) |
| GET | `/api/public/products/search` | Search & filter with pagination and sorting |
| GET | `/api/public/categories` | Category list |

### Admin Endpoints (Bearer JWT required)

| Method | Path | Description |
|---|---|---|
| POST | `/api/admin/products` | Create product |
| PUT | `/api/admin/products/{id}` | Update product |
| DELETE | `/api/admin/products/{id}` | Delete product |
| POST | `/api/admin/products/{id}/images` | Upload product images (multipart) |
| POST | `/api/admin/products/{id}/specifications` | Add technical specifications |
| GET | `/api/admin/categories` | List categories (admin) |
| POST | `/api/admin/categories` | Create category |

> For full request/response examples and Angular integration guide, see `API.md` and Swagger UI.

---

## Security

- All sensitive configuration (database credentials, JWT secret, mail server, API keys) loaded from environment variables
- JWT stateless authentication with access token expiry and refresh token rotation
- BCrypt password hashing
- OTP and reset tokens stored in Redis with TTL and attempt rate limiting
- See `SECURITY.md` for additional security notes and hardening recommendations

---

## Testing

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report
```

---

## Production Build

```bash
# Build executable JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/machinesshop-0.0.1-SNAPSHOT.jar
```

---

## Documentation

| File | Description |
|---|---|
| `API.md` | Detailed REST API specification + Angular integration guide |
| `QUICKSTART.md` | Quick start guide for local development |
| `IDE_SETUP.md` | IDE configuration (timezone, profiles) |
| `DEPLOYMENT.md` | Production deployment guide |
| `RAILWAY.md` | Deployment guide for Railway platform |
| `SECURITY.md` | Security notes and recommendations |

---

## Contact

- Email: `quancn27@gmail.com`

---
---

# Machine Shop API – Backend Spring Boot

## Tổng quan

Machine Shop API là dịch vụ backend cho ứng dụng mua bán máy móc, thiết bị công nghiệp.
Hệ thống cung cấp REST API bảo mật để xác thực người dùng, quản lý danh mục sản phẩm và tích hợp với frontend Angular.

Dự án sử dụng **Spring Boot 3**, tuân thủ kiến trúc backend hiện đại: kiến trúc phân lớp, DTO mapping, xử lý lỗi tập trung, bảo mật bằng JWT và tài liệu API rõ ràng với OpenAPI/Swagger.

---

## Công nghệ chính

| Hạng mục | Công nghệ |
|---|---|
| Ngôn ngữ | Java 17 |
| Framework | Spring Boot 3.5.10 |
| ORM & Persistence | Spring Data JPA (Hibernate) |
| Bảo mật | Spring Security + JWT (`jjwt 0.12.3`) + BCrypt |
| Database | PostgreSQL 15 (chính), H2 (test) |
| Messaging | RabbitMQ (hàng đợi email bất đồng bộ qua Spring AMQP) |
| Caching / Token Store | Redis (OTP, reset token, TTL, giới hạn số lần nhập sai) |
| Mapping đối tượng | MapStruct 1.5.5 + Lombok |
| Tài liệu API | SpringDoc OpenAPI 2.7.0 (Swagger UI) |
| Lưu trữ ảnh | Cloudinary HTTP client |
| Validation | Spring Boot Starter Validation (Bean Validation) |
| Cấu hình môi trường | `spring-dotenv` (hỗ trợ file `.env`) |
| Build | Maven + Spring Boot Maven Plugin |
| Testing | Spring Boot Test, Spring Security Test, H2 |

---

## Tính năng chính

### Xác thực & Phân quyền
- Đăng ký, đăng nhập với cơ chế JWT stateless
- Access token + refresh token
- Phân quyền theo role (admin vs. public)

### Quên mật khẩu (OTP)
- Luồng quên mật khẩu qua email OTP
- OTP 6 số, hiệu lực **5 phút**, lưu trên Redis
- Reset token hiệu lực **10 phút**, lưu trên Redis
- Tối đa **5 lần nhập sai OTP** trước khi phải yêu cầu mã mới

### Email chào mừng (bất đồng bộ)
- Tự động gửi email sau khi đăng ký thành công
- Xử lý bất đồng bộ qua hàng đợi **RabbitMQ** — không chặn thời gian phản hồi API

### Quản lý sản phẩm
**API công khai (không cần xác thực):**
- Danh sách sản phẩm có phân trang
- Tìm kiếm theo từ khóa
- Lọc theo loại sản phẩm và khoảng giá
- Chi tiết sản phẩm: cơ bản hoặc đầy đủ (ảnh, tên category, thông số kỹ thuật)
- Danh sách loại sản phẩm (dùng cho dropdown / filter)

**API Admin (yêu cầu JWT):**
- CRUD đầy đủ sản phẩm và loại sản phẩm
- Upload nhiều ảnh sản phẩm (`multipart/form-data`)
- Quản lý thông số kỹ thuật cho từng sản phẩm

### Xử lý lỗi chuẩn hóa
- Response lỗi thống nhất: HTTP status, mã lỗi (`code`), message, request path
- Danh sách lỗi validation theo từng field (nếu có)
- Mã lỗi thiết kế cho Angular interceptors: `TOKEN_EXPIRED`, `INVALID_TOKEN`, `INVALID_CREDENTIALS`

---

## Kiến trúc & Thiết kế

### Kiến trúc phân lớp
```
Controller  →  Service  →  Repository  →  PostgreSQL
                         ↘  Redis (OTP, reset token)
                         ↘  RabbitMQ (hàng đợi email)
                         ↘  Cloudinary (lưu trữ ảnh)
```

### DTO cho API
- DTO riêng cho: danh sách public, chi tiết sản phẩm, CRUD admin, tất cả luồng xác thực
- **MapStruct** để map entity ↔ DTO, kết hợp **Lombok** giảm boilerplate

### Thiết kế bảo mật
- JWT stateless (không dùng session phía server)
- Mật khẩu hash bằng BCrypt
- Toàn bộ thông tin nhạy cảm cấu hình qua biến môi trường

### Tích hợp Angular
- Format `ApiResponse<T>` thống nhất cho tất cả API admin
- Contract REST rõ ràng trong `API.md`
- Mã lỗi thiết kế để Angular HTTP interceptor dễ xử lý
- Hỗ trợ CORS và đường dẫn ảnh tương đối để dễ render frontend

---

## Bắt đầu sử dụng

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- Docker & Docker Compose (PostgreSQL, Redis, RabbitMQ)
- Git

### 1. Clone code

```bash
git clone <your-repo-url>
cd machinesshop
```

### 2. Cấu hình biến môi trường

```bash
cp .env.example .env
```

Chỉnh sửa `.env` với thông tin của bạn:

```properties
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_secure_secret_key_at_least_64_characters
# Thêm cấu hình Cloudinary, Redis, RabbitMQ, mail server nếu cần
```

### 3. Khởi động các dịch vụ

```bash
docker-compose up -d
```

### 4. Chạy ứng dụng

**Cách 1 – Maven Wrapper (khuyến nghị)**

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

**Cách 2 – Script**

```bash
# Windows
run.bat

# Linux / macOS
chmod +x run.sh && ./run.sh
```

**Cách 3 – Chạy từ IDE**

Đọc kỹ `IDE_SETUP.md` trước khi chạy từ IDE (bắt buộc để cấu hình timezone đúng).

Thiết lập profile:

```
SPRING_PROFILES_ACTIVE=dev
```

Profiles:
- `dev` — bật Swagger, log chi tiết
- `prod` — tắt Swagger, tối ưu

### 5. Truy cập

| Mục | URL |
|---|---|
| API base | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Health Check | `http://localhost:8080/api/health` |

---

## Tổng quan API

### Auth

| Method | Path | Mô tả |
|---|---|---|
| POST | `/api/auth/login` | Đăng nhập, nhận access + refresh token |
| POST | `/api/auth/register` | Đăng ký (gửi email chào mừng bất đồng bộ) |
| POST | `/api/auth/refresh` | Làm mới access token |
| POST | `/api/auth/forgot-password/request` | Yêu cầu OTP qua email |
| POST | `/api/auth/forgot-password/verify-otp` | Xác thực OTP, nhận reset token |
| POST | `/api/auth/forgot-password/reset` | Đặt lại mật khẩu bằng reset token |

### API công khai (không cần auth)

| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/health` | Health check |
| GET | `/api/public/products` | Danh sách sản phẩm |
| GET | `/api/public/products/{id}` | Chi tiết sản phẩm (cơ bản) |
| GET | `/api/public/products/detail/{id}` | Chi tiết sản phẩm (ảnh, thông số, category) |
| GET | `/api/public/products/search` | Tìm kiếm, lọc, phân trang, sắp xếp |
| GET | `/api/public/categories` | Danh sách loại sản phẩm |

### API Admin (yêu cầu Bearer JWT)

| Method | Path | Mô tả |
|---|---|---|
| POST | `/api/admin/products` | Tạo sản phẩm |
| PUT | `/api/admin/products/{id}` | Cập nhật sản phẩm |
| DELETE | `/api/admin/products/{id}` | Xóa sản phẩm |
| POST | `/api/admin/products/{id}/images` | Upload ảnh (multipart) |
| POST | `/api/admin/products/{id}/specifications` | Thêm thông số kỹ thuật |
| GET | `/api/admin/categories` | Danh sách loại sản phẩm (admin) |
| POST | `/api/admin/categories` | Tạo loại sản phẩm |

> Chi tiết request/response và hướng dẫn tích hợp Angular xem trong `API.md` và Swagger UI.

---

## Bảo mật

- Toàn bộ thông tin nhạy cảm (database, JWT secret, mail server, API key) cấu hình qua biến môi trường
- JWT stateless với access token có thời hạn và refresh token rotation
- Mật khẩu hash bằng BCrypt
- OTP và reset token lưu trên Redis với TTL và giới hạn số lần nhập sai
- Xem thêm trong `SECURITY.md`

---

## Kiểm thử

```bash
# Chạy toàn bộ test
mvn test

# Chạy test kèm coverage
mvn clean test jacoco:report
```

---

## Build & Deploy Production

```bash
# Build file JAR
mvn clean package -DskipTests

# Chạy JAR
java -jar target/machinesshop-0.0.1-SNAPSHOT.jar
```

---

## Tài liệu liên quan

| File | Mô tả |
|---|---|
| `API.md` | Đặc tả REST API chi tiết + hướng dẫn tích hợp Angular |
| `QUICKSTART.md` | Hướng dẫn nhanh cho môi trường local |
| `IDE_SETUP.md` | Cấu hình IDE (timezone, profiles) |
| `DEPLOYMENT.md` | Hướng dẫn triển khai production |
| `RAILWAY.md` | Hướng dẫn deploy lên Railway |
| `SECURITY.md` | Lưu ý bảo mật và khuyến nghị |

---

## Liên hệ

- Email: `quancn27@gmail.com`
