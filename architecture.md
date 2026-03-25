# 📚 Bookstore Application - Architecture Documentation

## 📋 Project Overview

**Application Name:** Bookstore API  
**Spring Boot Version:** 4.0.4  
**Java Version:** 21  
**Build Tool:** Maven  
**Database:** MySQL 8.0  
**Authentication:** JWT (JSON Web Tokens)  
**Architecture Pattern:** Layered Architecture (MVC)

---

## 🏗️ High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Applications                     │
│              (Web, Mobile, Third-Party APIs)                │
└─────────────────────┬───────────────────────────────────────┘
                      │ HTTP/REST + JWT
┌─────────────────────▼───────────────────────────────────────┐
│                   PRESENTATION LAYER                        │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │AuthController│  │BookController│  │CategoryCtrl  │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
│  ┌──────────────────────────────────────────────────┐      │
│  │     GlobalExceptionHandler                       │      │
│  └──────────────────────────────────────────────────┘      │
└─────────────────────┬───────────────────────────────────────┘
                      │ DTOs
┌─────────────────────▼───────────────────────────────────────┐
│                    BUSINESS LAYER                           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │ AuthService  │  │ BookService  │  │CategoryServ  │      │
│  │    (Impl)    │  │    (Impl)    │  │   (Impl)     │      │
│  └──────────────┘  └──────────────┘  └──────────────┘      │
└─────────────────────┬───────────────────────────────────────┘
                      │ Entities
┌─────────────────────▼───────────────────────────────────────┐
│                   PERSISTENCE LAYER                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐      │
│  │UserRepository│  │BookRepository│  │CategoryRepo  │      │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘      │
│         │   JPA/Hibernate  │                 │              │
└─────────┼──────────────────┼─────────────────┼──────────────┘
          │                  │                 │
┌─────────▼──────────────────▼─────────────────▼──────────────┐
│                    MySQL Database                           │
│  Tables: users, roles, books, categories, user_roles,       │
│          book_category                                      │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  CROSS-CUTTING CONCERNS                     │
│  • Security (JWT Filter, SecurityConfig)                    │
│  • Validation (Bean Validation)                             │
│  • Mapping (MapStruct)                                      │
│  • Monitoring (OpenTelemetry + Grafana)                     │
│  • Database Migration (Liquibase)                           │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Package Structure

```
com.bookstore/
│
├── BookstoreApplication.java        # Main entry point
│
├── controller/                      # REST API Controllers
│   ├── AuthController.java         # Authentication endpoints
│   ├── BookController.java         # Book CRUD operations
│   └── CategoryController.java     # Category CRUD operations
│
├── service/                         # Business Logic Layer
│   ├── auth/
│   │   ├── AuthService.java        # Auth interface
│   │   └── AuthServiceImpl.java    # Auth implementation
│   ├── book/
│   │   ├── BookService.java        # Book interface
│   │   └── BookServiceImpl.java    # Book implementation
│   └── category/
│       ├── CategoryService.java    # Category interface
│       └── CategoryServiceImpl.java
│
├── repository/                      # Data Access Layer (JPA)
│   ├── BookRepository.java
│   ├── CategoryRepository.java
│   ├── UserRepository.java
│   └── RoleRepository.java
│
├── entity/                          # JPA Entities
│   ├── User.java                   # User entity (with roles)
│   ├── Role.java                   # Role entity
│   ├── Book.java                   # Book entity (with categories)
│   └── Category.java               # Category entity
│
├── dto/                             # Data Transfer Objects
│   ├── Auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   └── UserResponse.java
│   ├── Book/
│   │   ├── BookRequest.java
│   │   └── BookResponse.java
│   └── Category/
│       ├── CategoryRequest.java
│       └── CategoryResponse.java
│
├── mapper/                          # MapStruct Mappers
│   ├── BookMapper.java             # Entity ↔ DTO conversion
│   └── CategoryMapper.java
│
├── security/                        # Security Configuration
│   ├── SecurityConfig.java         # Spring Security config
│   ├── JwtUtil.java                # JWT token utility
│   ├── JwtAuthenticationFilter.java# JWT filter
│   └── CustomUserDetailsService.java
│
└── exception/                       # Exception Handling
    ├── GlobalExceptionHandler.java # Global exception handler
    ├── ResourceNotFoundException.java
    └── ErrorResponse.java          # Error response DTO
```

---

## 🗄️ Data Model (ERD)

```
┌─────────────────────┐          ┌─────────────────────┐
│       users         │          │       roles         │
├─────────────────────┤          ├─────────────────────┤
│ id (PK)             │          │ id (PK)             │
│ username (unique)   │◄────┐    │ name (unique)       │
│ email (unique)      │     │    │ e.g., ROLE_USER,    │
│ password            │     │    │      ROLE_ADMIN     │
│ full_name           │     │    └─────────────────────┘
│ phone_number        │     │              ▲
│ enabled             │     │              │
│ account_non_locked  │     │    ┌─────────┴─────────┐
│ created_at          │     └────┤   user_roles      │
│ updated_at          │          │  (join table)     │
└─────────────────────┘          ├───────────────────┤
                                 │ user_id (FK)      │
                                 │ role_id (FK)      │
                                 └───────────────────┘

┌─────────────────────┐          ┌─────────────────────┐
│       books         │          │    categories       │
├─────────────────────┤          ├─────────────────────┤
│ id (PK)             │          │ id (PK)             │
│ title               │◄────┐    │ name (unique)       │
│ author              │     │    │ description         │
│ publisher           │     │    └─────────────────────┘
│ price               │     │              ▲
│ isbn                │     │              │
└─────────────────────┘     │    ┌─────────┴─────────┐
                            └────┤  book_category    │
                                 │  (join table)     │
                                 ├───────────────────┤
                                 │ book_id (FK)      │
                                 │ category_id (FK)  │
                                 └───────────────────┘
```

### Entity Relationships

- **User ↔ Role:** Many-to-Many (Eager loaded)
- **Book ↔ Category:** Many-to-Many

---

## 🛣️ API Endpoints

### Authentication Endpoints (Public)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/register` | Register new user | RegisterRequest | AuthResponse |
| POST | `/api/auth/login` | Login user | LoginRequest | AuthResponse |

### Book Endpoints (Protected - Requires JWT)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/books` | Get all books | - | List<BookResponse> |
| GET | `/api/books/{id}` | Get book by ID | - | BookResponse |
| POST | `/api/books` | Create new book | BookRequest | BookResponse |
| PUT | `/api/books/{id}` | Update book | BookRequest | BookResponse |
| DELETE | `/api/books/{id}` | Delete book | - | - |

### Category Endpoints (Protected - Requires JWT)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/categories` | Get all categories | - | List<CategoryResponse> |
| GET | `/api/categories/{id}` | Get category by ID | - | CategoryResponse |
| POST | `/api/categories` | Create new category | CategoryRequest | CategoryResponse |
| PUT | `/api/categories/{id}` | Update category | CategoryRequest | CategoryResponse |
| DELETE | `/api/categories/{id}` | Delete category | - | - |

---

## 🔐 Security Architecture

### JWT Authentication Flow

```
1. User Registration/Login
   ├─> AuthController receives credentials
   ├─> AuthService validates credentials
   ├─> JwtUtil generates JWT token
   └─> AuthResponse with token returned

2. Subsequent Requests
   ├─> Client sends request with "Authorization: Bearer {token}"
   ├─> JwtAuthenticationFilter intercepts request
   ├─> JwtUtil validates token
   ├─> CustomUserDetailsService loads user details
   ├─> SecurityContext is populated with authenticated user
   └─> Request proceeds to controller
```

### Security Configuration

- **Public Endpoints:** `/api/auth/**`
- **Protected Endpoints:** All other endpoints require valid JWT
- **Session Management:** Stateless (no server-side sessions)
- **Password Encoding:** BCryptPasswordEncoder
- **CORS:** Configured for `http://localhost:5173` (frontend)

### JWT Token Details

- **Algorithm:** HMAC SHA
- **Expiration:** 24 hours (86400000 ms)
- **Claims:** username, issued at, expiration
- **Secret Key:** Configured in `application.properties`

---

## 🔧 Key Technologies & Libraries

### Core Framework
- **Spring Boot 4.0.4**
  - Spring Web MVC
  - Spring Data JPA
  - Spring Security
  - Spring Validation

### Database & Persistence
- **MySQL 8.0** - Relational database
- **Hibernate** - ORM framework
- **Liquibase** - Database migration tool

### Security
- **Spring Security** - Authentication & authorization
- **JJWT 0.12.6** - JWT token generation & validation

### Code Generation & Utilities
- **Lombok** - Reduces boilerplate code
- **MapStruct 1.5.5** - Type-safe DTO mapping

### Development & DevOps
- **Spring Boot DevTools** - Hot reload
- **Spring Boot Docker Compose** - Container orchestration
- **OpenTelemetry** - Observability & monitoring
- **Grafana LGTM** - Metrics visualization

---

## 🐳 Docker Configuration

### Docker Compose Services

```yaml
services:
  grafana-lgtm:           # Monitoring & observability
    - Port: 3000 (Grafana UI)
    - Ports: 4317, 4318 (OpenTelemetry)
  
  mysql-db:               # Database
    - Image: mysql:8.0
    - Port: 3306
    - Database: bookstore
    - Credentials: root/123456
    - Volume: mysql_data (persistent storage)
```

**Lifecycle Management:** Spring Boot automatically starts/stops containers during development.

---

## ⚙️ Application Configuration

### Database Configuration
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore
spring.datasource.username=root
spring.datasource.password=123456
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Liquibase Configuration
```properties
spring.liquibase.enabled=true
spring.liquibase.changeLog=classpath:db/changelog/db.changelog-master.yaml
```

### JWT Configuration
```properties
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidation1234567890
jwt.expiration=86400000
```

### Docker Compose Integration
```properties
spring.docker.compose.file=compose.yaml
spring.docker.compose.lifecycle-management=start-and-stop
```

---

## 🎯 Design Patterns Used

1. **Layered Architecture**
   - Presentation Layer (Controllers)
   - Business Layer (Services)
   - Persistence Layer (Repositories)
   - Domain Layer (Entities)

2. **DTO Pattern**
   - Separate DTOs from entities
   - MapStruct for automatic conversion

3. **Repository Pattern**
   - JPA repositories abstract data access

4. **Dependency Injection**
   - Spring's IoC container manages dependencies

5. **Interface-Based Programming**
   - Services defined as interfaces with implementations

6. **Filter Pattern**
   - JwtAuthenticationFilter for request interception

7. **Builder Pattern**
   - Lombok @Builder for object construction

8. **Strategy Pattern**
   - Different authentication strategies (JWT, OAuth potential)

---

## 🚀 Build & Deployment

### Build Process
```bash
# Clean and build
./mvnw clean package

# Run the application
./mvnw spring-boot:run

# Build Docker image
docker build -t bookstore-api .
```

### Maven Configuration
- **Java Version:** 21
- **Annotation Processors:**
  - Lombok
  - MapStruct
  - Lombok-MapStruct Binding

---

## 📊 Data Flow Example

### Creating a Book

```
1. Client → POST /api/books
   Body: BookRequest (title, author, price, categoryIds)
   Header: Authorization: Bearer {jwt}

2. JwtAuthenticationFilter validates token

3. BookController receives request
   └─> Validates BookRequest

4. BookService.createBook()
   ├─> Loads categories by IDs (CategoryRepository)
   ├─> BookMapper converts BookRequest → Book entity
   ├─> Sets categories on book
   └─> BookRepository.save(book)

5. BookMapper converts Book entity → BookResponse

6. BookController returns BookResponse (201 Created)
```

---

## 🛡️ Error Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    → HTTP 404 NOT_FOUND
    
    @ExceptionHandler(Exception.class)
    → HTTP 500 INTERNAL_SERVER_ERROR
}
```

### Error Response Format
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Book with id 123 not found",
  "timestamp": "2026-03-24T02:39:30Z"
}
```

---

## 🧪 Testing Strategy

### Test Dependencies
- `spring-boot-starter-webmvc-test` - Controller testing
- `spring-security-test` - Security testing
- `spring-boot-starter-opentelemetry-test` - Telemetry testing

### Test Structure (Recommended)
```
src/test/java/
├── controller/    # Integration tests for controllers
├── service/       # Unit tests for services
├── repository/    # Repository tests
└── security/      # Security configuration tests
```

---

## 🔍 Monitoring & Observability

### OpenTelemetry Integration
- **Traces:** Request/response tracking
- **Metrics:** Application performance metrics
- **Logs:** Centralized logging

### Grafana Dashboard
- Access: `http://localhost:3000`
- Monitors: Database queries, API response times, error rates

---

## 📝 Best Practices Implemented

1. ✅ **Separation of Concerns:** Clear layer separation
2. ✅ **DRY Principle:** MapStruct eliminates repetitive mapping code
3. ✅ **Validation:** Input validation on DTOs
4. ✅ **Exception Handling:** Centralized error handling
5. ✅ **Security:** JWT-based stateless authentication
6. ✅ **Database Migrations:** Version-controlled schema with Liquibase
7. ✅ **Code Generation:** Lombok reduces boilerplate
8. ✅ **API Design:** RESTful conventions
9. ✅ **Eager Loading:** @EntityGraph prevents N+1 queries
10. ✅ **Configuration Management:** Externalized configuration

---

## 🔮 Future Enhancements (Potential)

- [ ] Add pagination and sorting for list endpoints
- [ ] Implement Redis caching for frequently accessed data
- [ ] Add API documentation with Swagger/OpenAPI
- [ ] Implement refresh token mechanism
- [ ] Add role-based access control (RBAC) for endpoints
- [ ] Implement file upload for book covers
- [ ] Add comprehensive unit and integration tests
- [ ] Implement CI/CD pipeline
- [ ] Add rate limiting
- [ ] Implement audit logging for sensitive operations

---

## 📞 Contact & Support

**Project:** Spring Boot Bookstore API  
**Academic:** Semester 6 Project  
**Technology Stack:** Spring Boot 4.0.4 + MySQL + JWT

---

*Last Updated: March 24, 2026*
