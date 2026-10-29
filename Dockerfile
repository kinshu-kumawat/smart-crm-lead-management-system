# ==============================================================================
# Multi-Stage Dockerfile for Smart CRM & Lead Management System
# Builds React Frontend and Spring Boot Backend into a Single Self-Contained Image
# ==============================================================================

# Stage 1: Build React Frontend
FROM node:20-alpine AS frontend-builder
WORKDIR /app/client

COPY client/package*.json ./
RUN npm ci

COPY client/ ./
RUN npm run build

# Stage 2: Build Spring Boot 3 Backend with Embedded React Assets
FROM maven:3.9.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app/server

COPY server/pom.xml ./
# Cache Maven dependencies
RUN mvn dependency:go-offline -B

COPY server/src ./src

# Copy compiled frontend static assets from Stage 1 into Spring Boot static directory
COPY --from=frontend-builder /app/server/src/main/resources/static ./src/main/resources/static

RUN mvn clean package -DskipTests

# Stage 3: Minimal Production Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add unprivileged user for security
RUN addgroup -S crmgroup && adduser -S crmuser -G crmgroup

COPY --from=backend-builder /app/server/target/smart-crm-backend-1.0.0.jar app.jar

RUN chown -R crmuser:crmgroup /app
USER crmuser

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:${PORT}/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
