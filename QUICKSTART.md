# 🚀 Quick Start Guide

## ✅ Ứng dụng đã được cấu hình đúng theo Best Practices!

### 🎯 Điểm chính:
- ✅ **UTC Timezone** - Chuẩn quốc tế
- ✅ **Environment Profiles** (dev/prod)
- ✅ **Secrets Management** (.env + GitHub Secrets)
- ✅ **Production Ready** Configuration
- ✅ **CI/CD** với GitHub Actions

---

## 🏃 Chạy ứng dụng (5 bước đơn giản)

### Bước 1: Start Database
```bash
docker-compose up -d
```

### Bước 2: Chạy app
```bash
# Windows - Dùng script
run.bat

# Hoặc manual
.\mvnw.cmd spring-boot:run
```

### Bước 3: Kiểm tra
Truy cập các URL sau:
- 🏥 Health: http://localhost:8080/api/health
- 👋 Welcome: http://localhost:8080/api/
- 📚 **Swagger**: http://localhost:8080/swagger-ui.html

---

## 🔧 Các lệnh quan trọng

### Development
```bash
# Start app
.\mvnw.cmd spring-boot:run

# Build
.\mvnw.cmd clean package

# Run tests
.\mvnw.cmd test
```

### Database
```bash
# Start
docker-compose up -d

# Stop
docker-compose down

# Reset (xóa data)
docker-compose down -v
docker stop postgres_java_spring
docker rm postgres_java_spring
docker-compose up -d
```

---

## ⚠️ LƯU Ý QUAN TRỌNG VỀ TIMEZONE

### Vấn đề
Windows có timezone "SE Asia Standard Time" (UTC+7) khiến PostgreSQL bị lỗi "Asia/Saigon"

### Giải pháp (đã được áp dụng)
1. ✅ File `pom.xml` đã có: `<jvmArguments>-Duser.timezone=UTC</jvmArguments>`
2. ✅ File `run.bat` đã set: `JAVA_TOOL_OPTIONS=-Duser.timezone=UTC`
3. ✅ PostgreSQL container dùng: `TZ=UTC`

### Kết quả
- Database lưu thời gian ở UTC
- Backend xử lý ở UTC
- Frontend sẽ convert sang timezone local của user

---

## 🌍 Hiển thị thời gian cho user Việt Nam

### Backend (Java)
```java
// Lưu vào DB (UTC)
LocalDateTime createdAt = LocalDateTime.now(ZoneOffset.UTC);

// Convert sang giờ VN khi cần
ZonedDateTime vnTime = createdAt.atZone(ZoneOffset.UTC)
    .withZoneSameInstant(ZoneId.of("Asia/Ho_Chi_Minh"));
```

### Frontend (JavaScript/TypeScript)
```javascript
// Browser tự động convert!
const date = new Date(apiResponse.createdAt);
console.log(date.toLocaleString('vi-VN')); 
// Output: "27/01/2026 06:24:29" (giờ VN)
```

---

## 🔐 Security Checklist

### Development (hiện tại)
- ✅ JWT secret từ environment variable
- ✅ Database password từ .env
- ✅ `.env` đã được gitignore
- ✅ Swagger enabled (dev only)

### Trước khi Deploy Production
- [ ] Generate JWT secret mới (64+ chars)
- [ ] Change database password
- [ ] Add secrets vào GitHub Actions
- [ ] Disable Swagger (đã config trong application-prod.properties)
- [ ] Setup HTTPS
- [ ] Setup firewall
- [ ] Backup database

---

## 📚 Tài liệu chi tiết

- **DEPLOYMENT.md** - Hướng dẫn deploy production đầy đủ
- **SECURITY.md** - Hướng dẫn bảo mật
- **README.md** - Tổng quan project

---

## 🐛 Troubleshooting

### Lỗi timezone
```bash
# Clean và rebuild
.\mvnw.cmd clean
docker-compose down -v
docker stop postgres_java_spring
docker rm postgres_java_spring
docker-compose up -d
.\mvnw.cmd spring-boot:run
```

### Port 8080 đã được sử dụng
```bash
# Tìm process
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F
```

### Database connection refused
```bash
# Check container
docker ps | grep postgres

# Restart
docker-compose restart postgres
```

---

## ✨ Features đã sẵn sàng

✅ Spring Boot 3.5.10
✅ Spring Security
✅ JWT Authentication (config sẵn sàng)
✅ PostgreSQL 15
✅ JPA/Hibernate
✅ Lombok
✅ Swagger/OpenAPI
✅ Spring Validation
✅ Health Check Endpoints
✅ Environment Profiles (dev/prod)
✅ Docker Support
✅ GitHub Actions CI/CD

---

## 🎯 Next Steps

1. **Tạo Repository & Service layers** cho entities
2. **Implement CRUD APIs** cho Product, Category, User, etc.
3. **Add Authentication & Authorization**
4. **Write Unit & Integration Tests**
5. **Setup Flyway** cho database migration
6. **Deploy to cloud** (AWS, GCP, Azure, DigitalOcean)

---

## 📞 Support

**App đã chạy thành công!** 🎉

Nếu gặp vấn đề:
1. Check logs: `docker-compose logs -f`
2. Check app health: http://localhost:8080/api/health
3. Xem DEPLOYMENT.md cho troubleshooting chi tiết
