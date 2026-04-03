# 📚 Bookstore Application - Architecture Documentation

## 📋 Project Overview

**Application Name:** Bookstore API  
**Spring Boot Version:** 4.0.4  
**Java Version:** 21  
**Build Tool:** Maven  
**Database:** MySQL 8.0  
**Authentication:** JWT (JSON Web Tokens) + OAuth2 (Google)  
**Architecture Pattern:** Layered Architecture (MVC)  
**Migration Tool:** Liquibase  
**Mapping:** MapStruct  
**Status:** ✅ Production-Ready Implementation Complete

---

## 🏗️ High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                     Client Applications                         │
│              (Web, Mobile, Third-Party APIs)                    │
└─────────────────────┬───────────────────────────────────────────┘
                      │ HTTP/REST + JWT + OAuth2
┌─────────────────────▼───────────────────────────────────────────┐
│                   SECURITY LAYER                                │
│  ┌──────────────────┐  ┌─────────────────────────────────────┐ │
│  │JwtAuthFilter     │  │ OAuth2 Success/Failure Handlers     │ │
│  │  (Token Valid.)  │  │ (Google OAuth2 Integration)         │ │
│  └──────────────────┘  └─────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────────┐
│                   PRESENTATION LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │AuthController│  │BookController│  │CategoryCtrl  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐                            │
│  │CartController│  │OrderController│                           │
│  └──────────────┘  └──────────────┘                            │
│  ┌──────────────────────────────────────────────────┐          │
│  │     GlobalExceptionHandler + Custom Exceptions   │          │
│  └──────────────────────────────────────────────────┘          │
└─────────────────────┬───────────────────────────────────────────┘
                      │ DTOs (MapStruct)
┌─────────────────────▼───────────────────────────────────────────┐
│                    BUSINESS LAYER                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ AuthService  │  │ BookService  │  │CategoryServ  │          │
│  │    (Impl)    │  │    (Impl)    │  │   (Impl)     │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ CartService  │  │ OrderService │  │RefreshToken  │          │
│  │    (Impl)    │  │    (Impl)    │  │Service (Impl)│          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
└─────────────────────┬───────────────────────────────────────────┘
                      │ Entities
┌─────────────────────▼───────────────────────────────────────────┐
│                   PERSISTENCE LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │UserRepository│  │BookRepository│  │CategoryRepo  │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │CartRepository│  │OrderRepository│  │RefreshToken  │          │
│  └──────────────┘  └──────────────┘  │Repository    │          │
│  ┌──────────────┐  ┌──────────────┐  └──────────────┘          │
│  │CartItemRepo  │  │OrderItemRepo │                             │
│  └──────┬───────┘  └──────┬───────┘                             │
│         │   JPA/Hibernate  │                                     │
└─────────┼──────────────────┼─────────────────────────────────────┘
          │                  │
┌─────────▼──────────────────▼─────────────────────────────────────┐
│               MySQL Database (Liquibase Migrations)             │
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
│   ├── AuthController.java         # Authentication (register, login, refresh)
│   ├── BookController.java         # Book CRUD operations (public + admin)
│   ├── CategoryController.java     # Category CRUD operations
│   ├── CartController.java         # Shopping cart management
│   └── OrderController.java        # Order placement & management
│
├── service/                         # Business Logic Layer
│   ├── auth/
│   │   ├── AuthService.java        # Auth interface
│   │   └── AuthServiceImpl.java    # Auth implementation
│   ├── book/
│   │   ├── BookService.java        # Book interface
│   │   └── BookServiceImpl.java    # Book implementation
│   ├── category/
│   │   ├── CategoryService.java    # Category interface
│   │   └── CategoryServiceImpl.java# Category implementation
│   ├── cart/
│   │   ├── CartService.java        # Cart interface
│   │   └── CartServiceImpl.java    # Cart implementation
│   ├── order/
│   │   ├── OrderService.java       # Order interface
│   │   └── OrderServiceImpl.java   # Order implementation
│   ├── user/
│   │   ├── UserService.java        # User management interface
│   │   └── UserServiceImpl.java    # User implementation
│   └── RefreshTokenService.java    # Token refresh service
│
├── repository/                      # Data Access Layer (JPA)
│   ├── BookRepository.java
│   ├── CategoryRepository.java
│   ├── UserRepository.java
│   ├── RoleRepository.java
│   ├── CartRepository.java
│   ├── CartItemRepository.java
│   ├── OrderRepository.java
│   ├── OrderItemRepository.java
│   ├── RefreshTokenRepository.java
│   └── OAuth2AccountRepository.java
│
├── entity/                          # JPA Entities
│   ├── User.java                   # User (with roles, eager fetch)
│   ├── Role.java                   # User roles (ADMIN, USER)
│   ├── Book.java                   # Book (with categories, soft delete)
│   ├── Category.java               # Category (soft delete)
│   ├── Cart.java                   # User's shopping cart
│   ├── CartItem.java               # Items in cart
│   ├── Order.java                  # Customer orders
│   ├── OrderItem.java              # Items in order
│   ├── OrderStatus.java            # Order status enum
│   ├── RefreshToken.java           # JWT refresh tokens
│   └── OAuth2Account.java          # OAuth2 linked accounts
│
├── dto/                             # Data Transfer Objects
│   ├── auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── TokenRefreshRequest.java
│   │   └── TokenRefreshResponse.java
│   ├── book/
│   │   ├── BookRequest.java
│   │   └── BookResponse.java
│   ├── category/
│   │   ├── CategoryRequest.java
│   │   └── CategoryResponse.java
│   ├── cart/
│   │   ├── AddToCartRequest.java
│   │   ├── UpdateCartItemRequest.java
│   │   ├── CartResponse.java
│   │   └── CartItemResponse.java
│   └── order/
│       ├── OrderRequest.java
│       ├── OrderResponse.java
│       ├── OrderItemRequest.java
│       └── OrderItemResponse.java
│
├── mapper/                          # MapStruct Mappers
│   ├── BookMapper.java             # Entity ↔ DTO conversion
│   ├── CategoryMapper.java
│   └── OrderMapper.java
│
├── security/                        # Security Configuration
│   ├── SecurityConfig.java         # Spring Security config (JWT + OAuth2)
│   ├── JwtUtil.java                # JWT token utility
│   ├── JwtAuthenticationFilter.java# JWT filter for stateless auth
│   ├── CustomUserDetailsService.java# Load user details
│   ├── UserPrincipal.java          # UserDetails + OAuth2User impl
│   ├── CustomOAuth2UserService.java# OAuth2 user handler
│   └── OAuth2AuthenticationSuccessHandler.java
│
├── config/                          # Application Configuration
│   └── AppConfig.java              # Auth beans (PasswordEncoder, etc.)
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
        │                        │ user_id (FK)      │
        │                        │ role_id (FK)      │
        ▼                        └───────────────────┘
┌─────────────────────┐
│   oauth2_users      │          ┌─────────────────────┐
├─────────────────────┤          │  refresh_tokens     │
│ id (PK)             │          ├─────────────────────┤
│ user_id (FK)        │          │ id (PK)             │
│ provider (google)   │          │ user_id (FK)        │
│ provider_user_id    │          │ token (unique)      │
│ email               │          │ expiry_date         │
│ created_at          │          │ revoked             │
└─────────────────────┘          │ created_at          │
                                 └─────────────────────┘

┌─────────────────────┐          ┌─────────────────────┐
│       books         │          │    categories       │
├─────────────────────┤          ├─────────────────────┤
│ id (PK)             │          │ id (PK)             │
│ title               │◄────┐    │ name (unique)       │
│ author              │     │    │ description         │
│ publisher           │     │    │ is_deleted          │
│ price               │     │    └─────────────────────┘
│ isbn                │     │              ▲
│ description         │     │              │
│ cover_image         │     │    ┌─────────┴─────────┐
│ quantity            │     └────┤  book_category    │
│ is_deleted          │          │  (join table)     │
└─────────────────────┘          ├───────────────────┤
        │                        │ book_id (FK)      │
        │                        │ category_id (FK)  │
        ▼                        └───────────────────┘
┌─────────────────────┐
│    cart_items       │          ┌─────────────────────┐
├─────────────────────┤          │   order_items       │
│ id (PK)             │          ├─────────────────────┤
│ cart_id (FK)        │          │ id (PK)             │
│ book_id (FK)        │          │ order_id (FK)       │
│ quantity            │          │ book_id (FK)        │
└─────────────────────┘          │ quantity            │
        ▲                        │ price               │
        │                        └─────────────────────┘
┌─────────────────────┐                  ▲
│       carts         │                  │
├─────────────────────┤          ┌───────┴───────────┐
│ id (PK)             │          │     orders        │
│ user_id (FK, uniq)  │          ├───────────────────┤
└─────────────────────┘          │ id (PK)           │
                                 │ user_id (FK)      │
                                 │ order_date        │
                                 │ total_amount      │
                                 │ status (enum)     │
                                 │ shipping_address  │
                                 │ phone_number      │
                                 └───────────────────┘
```

### Entity Relationships

- **User ↔ Role:** Many-to-Many (Eager loaded)
- **User ↔ Cart:** One-to-One
- **User ↔ Order:** One-to-Many
- **User ↔ RefreshToken:** One-to-Many
- **User ↔ OAuth2Account:** One-to-Many
- **Book ↔ Category:** Many-to-Many
- **Book ↔ CartItem:** One-to-Many
- **Book ↔ OrderItem:** One-to-Many
- **Cart ↔ CartItem:** One-to-Many
- **Order ↔ OrderItem:** One-to-Many

**Soft Delete Pattern:**
- Book and Category use `is_deleted` boolean flag for logical deletion

---

## 🛣️ API Endpoints

### 🔓 Authentication Endpoints (Public)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/auth/register` | Register new user | RegisterRequest | AuthResponse |
| POST | `/api/auth/login` | Login with username/email | LoginRequest | AuthResponse |
| POST | `/api/auth/refresh` | Refresh access token | TokenRefreshRequest | TokenRefreshResponse |

### 📚 Book Endpoints

| Method | Endpoint | Access | Description | Request Body | Response |
|--------|----------|--------|-------------|--------------|----------|
| GET | `/api/books` | Public | Get all books (pageable) | - | Page<BookResponse> |
| GET | `/api/books/{id}` | Public | Get book by ID | - | BookResponse |
| POST | `/api/books` | ADMIN | Create new book | BookRequest | BookResponse |
| PUT | `/api/books/{id}` | ADMIN | Update book | BookRequest | BookResponse |
| DELETE | `/api/books/{id}` | ADMIN | Delete book (soft) | - | - |

### 📑 Category Endpoints

| Method | Endpoint | Access | Description | Request Body | Response |
|--------|----------|--------|-------------|--------------|----------|
| GET | `/api/categories` | Public | Get all categories | - | List<CategoryResponse> |
| GET | `/api/categories/{id}` | Public | Get category by ID | - | CategoryResponse |
| POST | `/api/categories` | ADMIN | Create new category | CategoryRequest | CategoryResponse |
| PUT | `/api/categories/{id}` | ADMIN | Update category | CategoryRequest | CategoryResponse |
| DELETE | `/api/categories/{id}` | ADMIN | Delete category (soft) | - | - |

### 🛒 Cart Endpoints (Authenticated Users)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/cart` | Get user's cart | - | CartResponse |
| POST | `/api/cart/add` | Add item to cart | AddToCartRequest | CartResponse |
| PUT | `/api/cart/items/{id}` | Update cart item quantity | UpdateCartItemRequest | CartResponse |
| DELETE | `/api/cart/items/{id}` | Remove item from cart | - | - |

### 📦 Order Endpoints

#### User Endpoints (Authenticated)
| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/orders` | Place new order | OrderRequest | OrderResponse |
| GET | `/api/orders` | Get user's order history | - | List<OrderResponse> |
| GET | `/api/orders/{id}` | Get order details (user-scoped) | - | OrderResponse |
| PATCH | `/api/orders/{id}/cancel` | Cancel order | - | OrderResponse |

#### Admin Endpoints (ADMIN Only)
| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| GET | `/api/orders/admin` | Get all orders (pageable) | - | Page<OrderResponse> |
| GET | `/api/orders/admin/{id}` | Get any order details | - | OrderResponse |
| PATCH | `/api/orders/admin/{id}/status` | Update order status | status param | OrderResponse |

**Order Status Flow:** `PENDING` → `SHIPPING` → `DELIVERED` or `CANCELLED`

---

## 🔐 Security Architecture

### JWT + OAuth2 Authentication Flow

```
1. Traditional Authentication (Username/Password)
   ├─> POST /api/auth/register or /api/auth/login
   ├─> AuthService validates credentials (BCrypt)
   ├─> JwtUtil generates access token (15 min) & refresh token (7 days)
   └─> AuthResponse with both tokens returned

2. OAuth2 Authentication (Google)
   ├─> User clicks "Login with Google"
   ├─> Redirect to Google OAuth2 consent screen
   ├─> Google redirects back with authorization code
   ├─> CustomOAuth2UserService creates/links user account
   ├─> OAuth2AuthenticationSuccessHandler generates JWT
   └─> Redirect to frontend with JWT in URL

3. Subsequent API Requests
   ├─> Client sends "Authorization: Bearer {access_token}"
   ├─> JwtAuthenticationFilter intercepts request
   ├─> JwtUtil validates token signature & expiration
   ├─> CustomUserDetailsService loads user with roles
   ├─> SecurityContext populated with authenticated UserPrincipal
   └─> Request proceeds to controller with @PreAuthorize checks

4. Token Refresh Flow
   ├─> POST /api/auth/refresh with refresh token
   ├─> RefreshTokenService verifies token (not expired/revoked)
   ├─> JwtUtil generates new access token (15 min)
   └─> TokenRefreshResponse with new access token
```

### Security Configuration

**Public Endpoints:**
- `/api/auth/**` - Registration, login, token refresh
- `/api/books` (GET only) - Browse books without authentication
- `/api/books/{id}` (GET only) - View book details
- `/api/categories` (GET only) - Browse categories

**Authenticated Endpoints:**
- `/api/cart/**` - Shopping cart operations (user-scoped)
- `/api/orders` - Order placement & history (user-scoped)

**Admin-Only Endpoints:**
- `/api/books` (POST, PUT, DELETE) - Book management
- `/api/categories` (POST, PUT, DELETE) - Category management
- `/api/orders/admin/**` - Order administration

**Security Features:**
- Session Management: **STATELESS** (no server-side sessions)
- Password Encoding: **BCryptPasswordEncoder**
- CORS: Enabled for all origins with credentials support
- CSRF: Disabled (API mode with JWT)
- Method Security: `@PreAuthorize` annotations for role-based access

### JWT Token Configuration

**Access Token:**
- Algorithm: HMAC SHA with Base64-decoded secret
- Expiration: 900,000 ms (15 minutes)
- Claims: username, issued at, expiration
- Secret Key: Environment variable `JWT_SECRET`

**Refresh Token:**
- Expiration: 604,800,000 ms (7 days)
- Storage: Database table `refresh_tokens`
- Features: Revocation support, expiry date cleanup
- One-time use: Can be configured for rotation

**OAuth2 Configuration:**
- Provider: Google
- Client ID: `GOOGLE_CLIENT_ID` (environment variable)
- Client Secret: `GOOGLE_CLIENT_SECRET` (environment variable)
- Redirect URI: `http://localhost:8080/login/oauth2/code/google`
- Frontend Redirect: `http://localhost:3000/oauth2/redirect`

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
- **Spring Security 6** - Authentication & authorization
- **JJWT 0.12.6** - JWT token generation & validation
- **OAuth2 Client** - Google OAuth2 integration

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
jwt.secret=${JWT_SECRET}
jwt.expiration=900000           # 15 minutes
jwt.refresh-expiration=604800000 # 7 days
```

### OAuth2 Configuration
```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8080/login/oauth2/code/google
```

### Frontend Integration
```properties
app.frontend.login-url=http://localhost:3000/login
app.oauth2.redirect-uri=http://localhost:3000/oauth2/redirect
```

### Docker Compose Integration
```properties
spring.docker.compose.file=compose.yaml
spring.docker.compose.lifecycle-management=start-and-stop
```

---

## 📂 Database Migrations (Liquibase)

The database schema is version-controlled using **Liquibase** changesets. All migrations are located in:
```
src/main/resources/db/changelog/changes/
```

### Migration Changelog Sequence

| Order | File | Description |
|-------|------|-------------|
| 1 | `001-create-category-tables.yaml` | Creates `categories` table with soft delete |
| 2 | `002-create-book-tables.yaml` | Creates `books` table with soft delete |
| 3 | `003-create-book-category-table.yaml` | Creates `book_category` join table (Many-to-Many) |
| 4 | `004-insert-sample-datas.yaml` | Inserts sample books and categories data |
| 5 | `005-create-role-tables.yaml` | Creates `roles` table |
| 6 | `006-create-user-tables.yaml` | Creates `users` table with auth fields |
| 7 | `007-create-user-role-tables.yaml` | Creates `user_roles` join table (Many-to-Many) |
| 8 | `008-insert-roles-data.yaml` | Inserts default roles: ROLE_USER, ROLE_ADMIN |
| 9 | `009-create-cart-tables.yaml` | Creates `carts` table (OneToOne with users) |
| 10 | `010-create-cart-item-tables.yaml` | Creates `cart_items` table |
| 11 | `011-create-order-tables.yaml` | Creates `orders` table with status enum |
| 12 | `012-create-order-item-tables.yaml` | Creates `order_items` table |
| 13 | `013-create-refresh-token-tables.yaml` | Creates `refresh_tokens` table |
| 14 | `014-create-oauth2-account-tables.yaml` | Creates `oauth2_users` for OAuth2 integration |

### Key Features
- **Automatic Execution:** Liquibase runs on application startup
- **Version Control:** Each changeset has unique ID and author
- **Rollback Support:** Database changes can be rolled back
- **Database Agnostic:** Works across different databases
- **Validation Mode:** `ddl-auto=validate` ensures schema matches entities

### Master Changelog

```yaml
# db/changelog/db.changelog-master.yaml
databaseChangeLog:
  - include:
      file: db/changelog/changes/001-create-category-table.yaml
  - include:
      file: db/changelog/changes/002-create-book-table.yaml
  - include:
      file: db/changelog/changes/003-create-book-category-table.yaml
  - include:
      file: db/changelog/changes/004-insert-sample-datas.yaml
  - include:
      file: db/changelog/changes/005-create-role-table.yaml
  - include:
      file: db/changelog/changes/006-create-user-table.yaml
  - include:
      file: db/changelog/changes/007-create-user-role-table.yaml
  - include:
      file: db/changelog/changes/008-insert-roles-data.yaml
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
   - Entity builders for test data

8. **Strategy Pattern**
   - Multiple authentication strategies (JWT + OAuth2)

9. **Soft Delete Pattern**
   - Logical deletion with `is_deleted` flag on Book and Category

10. **User-Scoped Query Pattern**
    - Repository methods with user ID filtering for security

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

1. ✅ **Separation of Concerns:** Clear layer separation (Controller → Service → Repository)
2. ✅ **DRY Principle:** MapStruct eliminates repetitive mapping code
3. ✅ **Input Validation:** Bean validation on DTOs with @Valid
4. ✅ **Exception Handling:** Centralized error handling with @RestControllerAdvice
5. ✅ **Security:** JWT + OAuth2 stateless authentication
6. ✅ **Database Migrations:** Version-controlled schema with Liquibase
7. ✅ **Code Generation:** Lombok reduces boilerplate (@Data, @Builder, etc.)
8. ✅ **API Design:** RESTful conventions with proper HTTP methods
9. ✅ **N+1 Prevention:** @EntityGraph for eager loading of relationships
10. ✅ **Configuration Management:** Externalized configuration with environment variables
11. ✅ **Soft Deletes:** Logical deletion for data integrity
12. ✅ **Transactional Operations:** @Transactional for data consistency
13. ✅ **User-Scoped Data:** Repository queries filter by user ID for security
14. ✅ **Token Refresh:** Refresh token mechanism with revocation support

---

## ✨ Implemented Features Summary

### 🔐 Authentication & Authorization
- ✅ User registration with BCrypt password hashing
- ✅ User login (username or email)
- ✅ JWT access tokens (15 min expiration)
- ✅ JWT refresh tokens (7 days expiration)
- ✅ Google OAuth2 integration
- ✅ Role-based access control (ADMIN, USER)
- ✅ Method-level security with @PreAuthorize
- ✅ Stateless session management
- ✅ Token refresh endpoint
- ✅ Refresh token revocation support

### 📚 Book Management
- ✅ Browse books (public, paginated)
- ✅ View book details (public)
- ✅ Create books (ADMIN only)
- ✅ Update books (ADMIN only)
- ✅ Soft delete books (ADMIN only)
- ✅ Many-to-Many relationship with categories
- ✅ ISBN, cover image, quantity tracking
- ✅ EntityGraph optimization for category loading

### 📑 Category Management
- ✅ Browse categories (public)
- ✅ View category details (public)
- ✅ Create categories (ADMIN only)
- ✅ Update categories (ADMIN only)
- ✅ Soft delete categories (ADMIN only)
- ✅ Many-to-Many relationship with books

### 🛒 Shopping Cart
- ✅ Auto-create cart on first use
- ✅ Add items to cart
- ✅ Update item quantities
- ✅ Remove items from cart
- ✅ View cart with calculated totals
- ✅ User-scoped cart access

### 📦 Order Management
- ✅ Place orders from cart items
- ✅ Order history (user-scoped)
- ✅ View order details (user verification)
- ✅ Cancel orders (user-scoped)
- ✅ Order status workflow (PENDING → SHIPPING → DELIVERED)
- ✅ Admin: View all orders (paginated)
- ✅ Admin: Update order status
- ✅ Inventory reduction on order placement
- ✅ Stock validation
- ✅ Total amount calculation

### 🗄️ Database & Persistence
- ✅ MySQL 8.0 database
- ✅ Liquibase migrations (14 changesets)
- ✅ 11 JPA entities with relationships
- ✅ 10 repositories
- ✅ Soft delete pattern
- ✅ Automatic timestamps (createdAt, updatedAt)
- ✅ Index on refresh_token user_id

### 🛡️ Security Features
- ✅ CORS enabled for all origins
- ✅ CSRF disabled (API mode)
- ✅ BCrypt password encoding
- ✅ Custom UserDetailsService
- ✅ UserPrincipal (UserDetails + OAuth2User)
- ✅ JWT authentication filter
- ✅ OAuth2 success/failure handlers
- ✅ Public endpoints for browsing
- ✅ Protected endpoints for user operations
- ✅ Admin-only endpoints for management

### 🔧 Technical Features
- ✅ MapStruct entity-to-DTO mapping
- ✅ Lombok code generation
- ✅ Global exception handling
- ✅ Custom exceptions (ResourceNotFoundException, etc.)
- ✅ Docker Compose integration
- ✅ OpenTelemetry observability
- ✅ Grafana monitoring
- ✅ Spring Boot DevTools
- ✅ Pagination support (Spring Data Pageable)

---

## 🔮 Future Enhancements (Potential)

- [x] ~~Role-based access control (RBAC)~~ ✅ **Implemented**
- [x] ~~JWT refresh token mechanism~~ ✅ **Implemented**
- [x] ~~Shopping cart functionality~~ ✅ **Implemented**
- [x] ~~Order management system~~ ✅ **Implemented**
- [x] ~~OAuth2 Google integration~~ ✅ **Implemented**
- [x] ~~Pagination for lists~~ ✅ **Implemented**
- [x] Add search and filtering for books (by title, author, category, price range) ✅ **Implemented**
- [ ] Implement Redis caching for frequently accessed data
- [x] Add Swagger/OpenAPI documentation ✅ **Implemented**
- [ ] Implement file upload for book cover images (currently URL-based)
- [ ] Add comprehensive unit and integration tests
- [ ] Implement CI/CD pipeline (GitHub Actions)
- [ ] Add rate limiting (Bucket4j)
- [ ] Implement audit logging for sensitive operations
- [ ] Add wishlist functionality
- [ ] Implement book reviews and ratings
- [ ] Add payment gateway integration
- [ ] Email notifications for orders
- [ ] Advanced order tracking
- [ ] Admin dashboard analytics

---

## 📞 Project Information

**Project:** Spring Boot Bookstore API  
**Academic:** Semester 6 Project (BK)  
**Technology Stack:** Spring Boot 4.0.4 + MySQL 8.0 + JWT + OAuth2 + Swagger 
**Architecture:** Layered MVC with Spring Security  
**Status:** ✅ Production-Ready Implementation Complete

### Project Statistics
- **Controllers:** 5 (Auth, Book, Category, Cart, Order)
- **Services:** 7 interfaces with implementations
- **Repositories:** 10 JPA repositories
- **Entities:** 11 domain models
- **Database Tables:** 12 (including join tables)
- **Liquibase Changesets:** 14
- **API Endpoints:** 25+ RESTful endpoints
- **Lines of Code:** Comprehensive enterprise-grade application

---

*Last Updated: April 2, 2026*
