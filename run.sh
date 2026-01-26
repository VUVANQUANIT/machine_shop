#!/bin/bash
echo "Starting Machine Shop API..."
echo ""
echo "Make sure PostgreSQL is running (docker-compose up -d)"
echo ""
./mvnw spring-boot:run
