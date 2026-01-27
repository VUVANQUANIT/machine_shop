# Machine Shop - Backend API

Backend service cho ứng dụng Machine Shop được xây dựng với Spring Boot 3.

## 🚀 Công nghệ sử dụng

- **Java 17**
- **Spring Boot 3.5.10**
- **PostgreSQL 15**
- **Spring Security + JWT**
- **Spring Data JPA**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**

## 📋 Yêu cầu

- Java 17 hoặc cao hơn
- Maven 3.6+
- Docker & Docker Compose (cho PostgreSQL)
- Git

## ⚙️ Cài đặt và Chạy

### 1. Clone repository

```bash
git clone <your-repo-url>
cd machinesshop
```

### 2. Cấu hình môi trường

Copy file `.env.example` thành `.env` và cập nhật các giá trị:

```bash
cp .env.example .env
```

Chỉnh sửa `.env` với thông tin của bạn:

```properties
DATABASE_PASSWORD=your_secure_password
JWT_SECRET=your_very_long_secure_secret_key_at_least_64_characters
```

### 3. Khởi động PostgreSQL

```bash
docker-compose up -d
```

### 4. Build và chạy ứng dụng

#### Option 1: Sử dụng Maven Wrapper (khuyến nghị)

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw spring-boot:run
```

#### Option 2: Sử dụng script

```bash
# Windows
run.bat

# Linux/Mac
chmod +x run.sh
./run.sh
```

#### Option 3: Chạy từ IDE ⭐

**⚠️ QUAN TRỌNG**: Đọc file **[IDE_SETUP.md](IDE_SETUP.md)** để cấu hình IDE đúng cách!

- **IntelliJ IDEA**: Run configuration đã được tạo sẵn
- **VS Code**: Launch configuration đã được tạo sẵn

Nếu không config, bạn sẽ gặp lỗi timezone khi chạy từ IDE!

### 5. Truy cập ứng dụng

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html
- API Docs: http://localhost:8080/v3/api-docs
- Health Check: http://localhost:8080/api/health

## 🗃️ Database Schema

### Entities

1. **Product** - Thông tin sản phẩm/máy móc
2. **Category** - Danh mục sản phẩm
3. **ProductImage** - Hình ảnh sản phẩm
4. **ProductSpecification** - Thông số kỹ thuật
5. **User** - Người dùng (Admin)

## 🔒 Bảo mật

- ⚠️ **QUAN TRỌNG**: Đọc file [SECURITY.md](SECURITY.md) để biết về các vấn đề bảo mật và cách xử lý
- Sử dụng environment variables cho tất cả thông tin nhạy cảm
- JWT authentication
- BCrypt password hashing

## 🧪 Testing

```bash
# Chạy tất cả tests
mvn test

# Chạy tests với coverage
mvn clean test jacoco:report
```

## 📦 Build Production

```bash
# Build JAR file
mvn clean package -DskipTests

# Chạy JAR
java -jar target/machinesshop-0.0.1-SNAPSHOT.jar
```

## 🔄 CI/CD

Project sử dụng GitHub Actions cho CI/CD. Workflow tự động chạy khi:
- Push lên branch `main` hoặc `develop`
- Tạo Pull Request vào `main` hoặc `develop`

### Cấu hình GitHub Secrets

Thêm các secrets sau vào GitHub repository:
1. Vào Settings → Secrets and variables → Actions
2. Thêm secret mới:
   - `JWT_SECRET`: JWT secret key của bạn (tối thiểu 64 ký tự)

## 📝 API Endpoints (Sẽ được cập nhật)

Xem đầy đủ API documentation tại Swagger UI khi ứng dụng đang chạy:
http://localhost:8080/swagger-ui.html

## 🛠️ Development

### Code style
- Sử dụng Lombok để giảm boilerplate code
- Follow Spring Boot best practices
- RESTful API design

### Database migrations
- JPA auto DDL: `spring.jpa.hibernate.ddl-auto=update` (dev)
- Trong production nên sử dụng Flyway hoặc Liquibase

## 📚 Tài liệu

- **[QUICKSTART.md](QUICKSTART.md)** - Hướng dẫn nhanh để bắt đầu
- **[IDE_SETUP.md](IDE_SETUP.md)** - ⭐ **QUAN TRỌNG**: Cấu hình IDE để chạy không lỗi
- **[DEPLOYMENT.md](DEPLOYMENT.md)** - Hướng dẫn deploy production đầy đủ
- **[SECURITY.md](SECURITY.md)** - Cảnh báo bảo mật và cách xử lý

## ⚠️ Lưu ý quan trọng

### Timezone Configuration

Ứng dụng sử dụng **UTC timezone** (best practice). Nếu bạn chạy từ IDE và gặp lỗi timezone, hãy đọc **[IDE_SETUP.md](IDE_SETUP.md)** để cấu hình đúng.

### Environment Profiles

- **dev**: Development mode (Swagger enabled, debug logging)
- **prod**: Production mode (Swagger disabled, optimized)

Set profile bằng biến môi trường: `SPRING_PROFILES_ACTIVE=dev`

## 📧 Liên hệ

[quancn27@gmail.com]

## 📄 License

[License của bạn]
