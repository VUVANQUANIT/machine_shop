# 🚀 Deployment Guide - Best Practices

## 📋 Overview

Ứng dụng này được cấu hình theo **best practices** cho production deployment với:
- ✅ Environment-based configuration (dev/prod)
- ✅ UTC timezone (chuẩn quốc tế)
- ✅ Secure secrets management
- ✅ Production-optimized settings
- ✅ CI/CD ready với GitHub Actions

---

## 🏗️ Architecture

### Timezone Strategy (Best Practice)
```
┌─────────────┐
│  Database   │  ← Store in UTC (Universal Time)
│   (UTC)     │
└──────┬──────┘
       │
┌──────▼──────┐
│   Backend   │  ← Process in UTC
│   (UTC)     │
└──────┬──────┘
       │
┌──────▼──────┐
│  Frontend   │  ← Display in user's timezone
│  (Local TZ) │     (Browser auto-converts)
└─────────────┘
```

**Lý do dùng UTC:**
- ✅ Tránh lỗi Daylight Saving Time (DST)
- ✅ Dễ scale globally (nhiều quốc gia)
- ✅ Chuẩn quốc tế, tương thích tốt
- ✅ Frontend tự động convert sang timezone local

---

## 🔧 Environment Profiles

### Development (`dev`)
```bash
# File: application-dev.properties
- DDL Auto: update (tự tạo/sửa bảng)
- Show SQL: true (debug queries)
- Swagger: enabled
- Logging: DEBUG level
- Connection Pool: Small (5-10 connections)
```

### Production (`prod`)
```bash
# File: application-prod.properties
- DDL Auto: validate (không sửa DB, an toàn)
- Show SQL: false (performance)
- Swagger: DISABLED (security)
- Logging: INFO/WARN only
- Connection Pool: Large (10-20 connections)
```

---

## 🚀 Local Development

### 1. Setup Environment

```bash
# Copy example env file
cp .env.example .env

# Edit .env with your values
# IMPORTANT: Use SPRING_PROFILES_ACTIVE=dev
```

### 2. Start Database

```bash
docker-compose up -d
```

### 3. Run Application

**Option A: Maven Wrapper (Recommended)**
```bash
# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

**Option B: Use script**
```bash
# Windows
run.bat

# Linux/Mac
./run.sh
```

### 4. Access Application

- API: http://localhost:8080
- Health Check: http://localhost:8080/api/health
- **Swagger UI: http://localhost:8080/swagger-ui.html** (dev only!)

---

## 📦 Production Deployment

### 1. Prepare Environment Variables

Trên server production, tạo `.env` file:

```bash
# Production .env
DATABASE_URL=jdbc:postgresql://prod-db-host:5432/machineshop
DATABASE_USERNAME=prod_user
DATABASE_PASSWORD=super_secure_password_here

JWT_SECRET=<generate with: openssl rand -base64 64>
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

SERVER_PORT=8080
SPRING_PROFILES_ACTIVE=prod
```

### 2. Database Migration (IMPORTANT!)

**⚠️ Production sử dụng `ddl-auto=validate`** (không tự động tạo bảng)

Sử dụng **Flyway** hoặc **Liquibase** cho production:

```bash
# Install Flyway (recommended)
# Add to pom.xml:
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>

# Create migration scripts in src/main/resources/db/migration/
# V1__init_schema.sql
# V2__add_indexes.sql
```

### 3. Build for Production

```bash
# Build JAR file
.\mvnw.cmd clean package -DskipTests

# Or with tests
.\mvnw.cmd clean package

# Output: target/machinesshop-0.0.1-SNAPSHOT.jar
```

### 4. Run in Production

```bash
# Method 1: Direct JAR execution
java -jar target/machinesshop-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod

# Method 2: With explicit config
java -jar target/machinesshop-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod \
  --spring.config.location=file:./application-prod.properties

# Method 3: Docker (recommended)
# See docker-compose.prod.yml
```

---

## 🐳 Docker Production Deployment

### Create Production Docker Compose

```yaml
# docker-compose.prod.yml
version: '3.8'

services:
  postgres:
    image: postgres:15
    container_name: machinesshop_db_prod
    environment:
      POSTGRES_DB: ${DATABASE_NAME:-machineshop}
      POSTGRES_USER: ${DATABASE_USERNAME}
      POSTGRES_PASSWORD: ${DATABASE_PASSWORD}
      TZ: UTC
      PGTZ: UTC
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
    restart: unless-stopped
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${DATABASE_USERNAME}"]
      interval: 10s
      timeout: 5s
      retries: 5

  app:
    build: .
    container_name: machinesshop_app
    depends_on:
      postgres:
        condition: service_healthy
    environment:
      SPRING_PROFILES_ACTIVE: prod
      DATABASE_URL: jdbc:postgresql://postgres:5432/${DATABASE_NAME:-machineshop}
      DATABASE_USERNAME: ${DATABASE_USERNAME}
      DATABASE_PASSWORD: ${DATABASE_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
    ports:
      - "${SERVER_PORT:-8080}:8080"
    restart: unless-stopped

volumes:
  postgres_data:
```

### Deploy with Docker

```bash
# Build and start
docker-compose -f docker-compose.prod.yml up -d

# View logs
docker-compose -f docker-compose.prod.yml logs -f app

# Stop
docker-compose -f docker-compose.prod.yml down
```

---

## 🔐 Security Checklist

### Before Deploying to Production:

- [ ] **JWT Secret**: Generate new 64+ character secret
  ```bash
  openssl rand -base64 64
  ```
  
- [ ] **Database Password**: Use strong password (16+ chars)

- [ ] **Environment Variables**: Never commit `.env` to git

- [ ] **GitHub Secrets**: Add all secrets to repository
  - Go to: Settings → Secrets → Actions
  - Add: `JWT_SECRET`, `DB_PASSWORD`, etc.

- [ ] **Remove Secrets from Git History**: 
  ```bash
  # See SECURITY.md for instructions
  ```

- [ ] **Swagger**: Disabled in production (`application-prod.properties`)

- [ ] **HTTPS**: Use reverse proxy (Nginx) with SSL certificate

- [ ] **Firewall**: Only expose necessary ports (80, 443)

- [ ] **Database**: Use managed database service (AWS RDS, DigitalOcean, etc.)

---

## 🔄 CI/CD with GitHub Actions

Workflow file đã được tạo: `.github/workflows/ci-cd.yml`

### Setup GitHub Secrets:

1. Go to GitHub repository → Settings → Secrets and variables → Actions
2. Add the following secrets:

```
JWT_SECRET=<your-64-char-secret>
```

### Workflow triggers:
- ✅ Push to `main` or `develop` branch
- ✅ Pull Request to `main` or `develop`

### Workflow steps:
1. Checkout code
2. Setup JDK 17
3. Build with Maven
4. Run tests
5. Upload artifact

---

## 📊 Monitoring & Logging

### Health Check Endpoints

```bash
# Development
GET http://localhost:8080/actuator/health
GET http://localhost:8080/actuator/info
GET http://localhost:8080/actuator/metrics

# Production (limited)
GET http://your-domain.com/actuator/health
GET http://your-domain.com/actuator/metrics
```

### Log Levels

**Development:**
```properties
logging.level.com.example.machinesshop=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

**Production:**
```properties
logging.level.root=INFO
logging.level.com.example.machinesshop=INFO
logging.level.org.hibernate.SQL=WARN
```

---

## 🧪 Testing

```bash
# Run all tests
.\mvnw.cmd test

# Run with coverage
.\mvnw.cmd clean test jacoco:report

# Integration tests only
.\mvnw.cmd test -Dtest=*IntegrationTest
```

---

## 🆘 Troubleshooting

### Issue: Timezone errors

**Solution:**
```bash
# Clean everything and restart
docker-compose down -v
docker stop <container_name>
docker rm <container_name>
.\mvnw.cmd clean
docker-compose up -d
.\mvnw.cmd spring-boot:run
```

### Issue: Port already in use

**Solution:**
```bash
# Windows - Kill process on port 8080
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Issue: Database connection refused

**Solution:**
```bash
# Check PostgreSQL is running
docker ps | grep postgres

# Check connection
docker exec -it postgres_java_spring psql -U postgres -d machineshop

# Restart database
docker-compose restart postgres
```

---

## 📚 Best Practices Applied

✅ **12-Factor App Compliance**
- Config in environment variables
- Separate build and run stages
- Export services via port binding

✅ **Security**
- No secrets in code
- Environment-based configuration
- JWT token authentication

✅ **Performance**
- Connection pooling optimized
- Query logging only in dev
- Lazy loading for entities

✅ **Scalability**
- Stateless application
- UTC timezone
- Docker containerization

✅ **Maintainability**
- Clear separation of concerns
- Profile-based configuration
- Comprehensive documentation

---

## 🎯 Next Steps

1. ✅ Setup Flyway for database migrations
2. ✅ Add comprehensive error handling
3. ✅ Implement request/response logging
4. ✅ Add rate limiting
5. ✅ Setup monitoring (Prometheus/Grafana)
6. ✅ Add integration tests
7. ✅ Configure CORS properly
8. ✅ Setup reverse proxy (Nginx)
9. ✅ Get SSL certificate (Let's Encrypt)
10. ✅ Setup backup strategy for database

---

## 📞 Support

For issues or questions:
- Check logs: `docker-compose logs -f`
- Read SECURITY.md for security issues
- Check GitHub Actions for CI/CD failures

---

**Remember:** Always use `prod` profile for production and keep secrets secure! 🔐
