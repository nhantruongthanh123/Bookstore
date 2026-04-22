# Bookstore Backend

## Link demo

[https://bookshop-peach.vercel.app/](https://bookshop-peach.vercel.app/)

## Project structure

```text
bookstore/
|- src/
|  |- main/
|  |  |- java/com/bookstore/
|  |  |  |- config/
|  |  |  |- controller/
|  |  |  |- dto/
|  |  |  |- entity/
|  |  |  |- exception/
|  |  |  |- mapper/
|  |  |  |- repository/
|  |  |  |- security/
|  |  |  `- service/
|  |  `- resources/
|  |     |- db/changelog/
|  |     `- application*.properties
|  `- test/
|     |- java/com/bookstore/
|     `- resources/
|- compose.yaml
|- pom.xml
`- mvnw.cmd
```

## Tech stack

- Java 21
- Spring Boot 4
- Spring Web, Spring Security (JWT + OAuth2)
- Spring Data JPA + MySQL
- Liquibase
- Redis Cache
- MapStruct + Lombok
- OpenAPI (springdoc)
- Docker Compose

## Prerequisites

- JDK 21
- Docker Desktop (for MySQL and Redis via `compose.yaml`)
- Git (optional, for clone)

## Local setup

1. Clone and open project folder.
2. Set environment variables:
   - `JWT_SECRET`
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`
   - `CLOUD_NAME`
   - `CLOUD_API_KEY`
   - `CLOUD_API_SECRET`
3. Start services:
   ```powershell
   docker compose up -d
   ```
4. Run backend:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
5. API runs at `http://localhost:8080`.
