# ============================================
# Stage 1: Build
# ============================================
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /build

# Copy Maven wrapper and pom
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer if pom unchanged)
RUN ./mvnw dependency:go-offline -B

# Copy source and build JAR (skip tests for faster build)
COPY src src
RUN ./mvnw package -DskipTests -B

# ============================================
# Stage 2: Run
# ============================================
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Non-root user
RUN adduser -D -u 1000 appuser

# Copy JAR from builder (Spring Boot repackages to single jar)
COPY --from=builder /build/target/machinesshop-*.jar app.jar

RUN chown -R appuser:appuser /app

USER appuser

# Railway injects PORT at runtime - Spring Boot reads server.port
# Timezone UTC (tránh lỗi Asia/Saigon khi chạy trong container)
EXPOSE 8080

CMD ["sh", "-c", "java -Duser.timezone=UTC -jar app.jar --server.port=${PORT:-8080}"]
