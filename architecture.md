# 📚 Bookstore Application - Architecture Documentation

## 📋 Project Overview

**Application Name:** Bookstore API  
**Spring Boot Version:** 4.0.4  
**Java Version:** 21  
**Build Tool:** Maven  
**Database:** MySQL 8.0  
**Cache:** Redis  
**File Storage:** Cloudinary  
**Authentication:** JWT (JSON Web Tokens) + OAuth2 (Google)  
**Architecture Pattern:** Layered Architecture (MVC)  
**Migration Tool:** Liquibase  
**Mapping:** MapStruct  
**API Documentation:** OpenAPI 3.0 (Swagger UI)  
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
│  │  (Token Valid.)  │  │ (Google OAuth2 + Cookie Management) │ │
│  └──────────────────┘  └─────────────────────────────────────┘ │
└─────────────────────┬───────────────────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────────────────┐
│                   PRESENTATION LAYER                            │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │AuthController│  │BookController│  │CategoryCtrl  │          │
│  │              │  │(+ Search)    │  │              │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │CartController│  │OrderController│ │FileUploadCtrl│          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────────────────────────────────────────┐          │
│  │     GlobalExceptionHandler + Custom Exceptions   │          │
│  └──────────────────────────────────────────────────┘          │
└─────────────────────┬───────────────────────────────────────────┘
                      │ DTOs (MapStruct + PageResponse)
┌─────────────────────▼───────────────────────────────────────────┐
│                    BUSINESS LAYER                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ AuthService  │  │ BookService  │  │CategoryServ  │          │
│  │    (Impl)    │  │  (Impl +     │  │   (Impl)     │          │
│  │              │  │  Cacheable)  │  │(Redis Cache) │          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐          │
│  │ CartService  │  │ OrderService │  │RefreshToken  │          │
│  │    (Impl)    │  │    (Impl)    │  │Service (Impl)│          │
│  └──────────────┘  └──────────────┘  └──────────────┘          │
│  ┌──────────────────────────┐                                   │
│  │ CloudinaryService (Impl) │                                   │
│  │  (File Upload to CDN)    │                                   │
│  └──────────────────────────┘                                   │
└─────────────────────┬───────────────────────────────────────────┘
                      │ Entities + Specifications
┌─────────────────────▼───────────────────────────────────────────┐
│                   PERSISTENCE LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │UserRepository│  │BookRepository│  │CategoryRepo  │           │
│  └──────────────┘  └──────────────┘  └──────────────┘           │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐           │
│  │CartRepository│  │OrderRepository│  │RefreshToken  │          │
│  └──────────────┘  └──────────────┘  │Repository    │           │
│  ┌──────────────┐  ┌──────────────┐  └──────────────┘           │
│  │CartItemRepo  │  │OrderItemRepo │  ┌──────────────┐           │
│  │              │  │              │  │BookSpec      │           │
│  │              │  │              │  │(Dynamic Query│           │
│  └──────┬───────┘  └──────┬───────┘  └──────────────┘           │
│         │   JPA/Hibernate  │                                     │
└─────────┼──────────────────┼─────────────────────────────────────┘
          │                  │
          │                  │
┌─────────▼──────────────────▼─────────────────────────────────────┐
│               MySQL Database (Liquibase Migrations)             │
│  Tables: users, roles, books, categories, user_roles,           │
│          book_category, carts, cart_items, orders, order_items, │
│          refresh_tokens, oauth2_accounts                        │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     EXTERNAL SERVICES                           │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐             │
│  │ Redis Cache │  │  Cloudinary │  │Google OAuth2│             │
│  │ (Categories,│  │  (Image CDN)│  │  Provider   │             │
│  │  Books)     │  │             │  │             │             │
│  └─────────────┘  └─────────────┘  └─────────────┘             │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  CROSS-CUTTING CONCERNS                     │
│  • Security (JWT Filter, SecurityConfig)                    │
│  • Validation (Bean Validation)                             │
│  • Mapping (MapStruct)                                      │
│  • Database Migration (Liquibase)                           │
│  • Caching (Redis with @Cacheable)                          │
│  • API Documentation (OpenAPI/Swagger)                      │
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
│   ├── BookController.java         # Book CRUD + search operations (public + admin)
│   ├── CategoryController.java     # Category CRUD operations
│   ├── CartController.java         # Shopping cart management
│   ├── OrderController.java        # Order placement & management
│   └── FileUploadController.java   # File upload to Cloudinary
│
├── service/                         # Business Logic Layer
│   ├── auth/
│   │   ├── AuthService.java        # Auth interface
│   │   ├── AuthServiceImpl.java    # Auth implementation
│   │   ├── RefreshTokenService.java# Token refresh interface
│   │   └── RefreshTokenServiceImpl.java # Token refresh implementation
│   ├── book/
│   │   ├── BookService.java        # Book interface
│   │   └── BookServiceImpl.java    # Book implementation (with search)
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
│   └── cloudinary/
│       ├── CloudinaryService.java  # File upload interface
│       └── CloudinaryServiceImpl.java # Cloudinary implementation
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
│   ├── Auth/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── UserResponse.java
│   │   ├── OAuth2UserInfo.java
│   │   ├── TokenRefreshRequest.java
│   │   └── TokenRefreshResponse.java
│   ├── Book/
│   │   ├── BookRequest.java
│   │   ├── BookResponse.java
│   │   └── SearchBookRequest.java
│   ├── Category/
│   │   ├── CategoryRequest.java
│   │   └── CategoryResponse.java
│   ├── Cart/
│   │   ├── AddToCartRequest.java
│   │   ├── UpdateCartItemRequest.java
│   │   ├── CartResponse.java
│   │   └── CartItemResponse.java
│   ├── Order/
│   │   ├── OrderRequest.java
│   │   └── OrderResponse.java
│   ├── OrderItem/
│   │   ├── OrderItemRequest.java
│   │   └── OrderItemResponse.java
│   └── Page/
│       └── PageResponse.java        # Generic pagination wrapper
│
├── mapper/                          # MapStruct Mappers
│   ├── BookMapper.java             # Book Entity ↔ DTO conversion
│   ├── CategoryMapper.java         # Category Entity ↔ DTO conversion
│   └── OrderMapper.java            # Order Entity ↔ DTO conversion
│
├── repository/spec/                 # JPA Specifications
│   └── BookSpecification.java      # Dynamic query builder for book search
│
├── security/                        # Security Configuration
│   ├── SecurityConfig.java         # Spring Security config (JWT + OAuth2)
│   ├── JwtUtil.java                # JWT token utility
│   ├── JwtAuthenticationFilter.java# JWT filter for stateless auth
│   ├── CustomUserDetailsService.java# Load user details
│   ├── UserPrincipal.java          # UserDetails + OAuth2User impl
│   ├── AuthenticationUtil.java     # Helper for getting current user
│   └── oauth2/                     # OAuth2 Integration Package
│       ├── CustomOAuth2UserService.java # OAuth2 user handler
│       ├── OAuth2UserService.java  # OAuth2 service interface
│       ├── OAuth2UserServiceImpl.java # OAuth2 service implementation
│       ├── OAuth2AuthenticationSuccessHandler.java
│       ├── OAuth2AuthenticationFailureHandler.java
│       ├── HttpCookieOAuth2AuthorizationRequestRepository.java
│       └── CookieUtils.java        # Cookie management utilities
│
├── config/                          # Application Configuration
│   ├── AppConfig.java              # Auth beans (PasswordEncoder, etc.)
│   ├── CloudinaryConfig.java       # Cloudinary file upload configuration
│   ├── RedisConfig.java            # Redis cache configuration
│   └── OpenApiConfig.java          # Swagger/OpenAPI documentation config
│
└── exception/                       # Exception Handling
    ├── GlobalExceptionHandler.java # Global exception handler (@ControllerAdvice)
    ├── ResourceNotFoundException.java # 404 errors
    ├── DuplicateResourceException.java # Duplicate entry errors
    ├── UnauthorizedException.java  # 401 authentication errors
    └── ErrorResponse.java          # Standard error response DTO
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
│       books         │          │      authors        │
├─────────────────────┤          ├─────────────────────┤
│ id (PK)             │◄────┐    │ id (PK)             │
│ title               │     │    │ name (unique)       │
│ publisher           │     │    │ description         │
│ price               │     │    └─────────────────────┘
│ isbn                │     │              ▲
│ description         │     │              │
│ cover_image         │     │    ┌─────────┴─────────┐
│ quantity            │     └────┤   book_author     │
│ is_deleted          │          │   (join table)    │
└─────────────────────┘          ├───────────────────┤
        │                        │ book_id (FK)      │
        │                        │ author_id (FK)    │
        ▼                        └───────────────────┘
┌─────────────────────┐
│    categories       │
├─────────────────────┤
│ id (PK)             │
│ name (unique)       │
│ description         │
│ is_deleted          │
└─────────────────────┘
        ▲
        │
┌───────┴───────────┐
│   book_category   │
│   (join table)    │
├───────────────────┤
│ book_id (FK)      │
│ category_id (FK)  │
└───────────────────┘
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
- **Book ↔ Author:** Many-to-Many
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
| GET | `/oauth2/authorization/google` | Initiate Google OAuth2 login | - | Redirect to Google |
| GET | `/login/oauth2/code/google` | OAuth2 callback endpoint | - | Redirect with JWT |

### 📤 File Upload Endpoints (Public)

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/files/upload` | Upload image to Cloudinary | MultipartFile | `{"url": "cloudinary_url"}` |

### 📚 Book Endpoints

| Method | Endpoint | Access | Description | Request Body | Response |
|--------|----------|--------|-------------|--------------|----------|
| GET | `/api/books` | Public | Get all books (pageable, default size=5) | - | PageResponse<BookResponse> |
| GET | `/api/books/{id}` | Public | Get book by ID | - | BookResponse |
| GET | `/api/books/search` | Public | Search books by title, author, category, price range | Query params | PageResponse<BookResponse> |
| POST | `/api/books` | ADMIN | Create new book | BookRequest | BookResponse |
| PUT | `/api/books/{id}` | ADMIN | Update book | BookRequest | BookResponse |
| DELETE | `/api/books/{id}` | ADMIN | Delete book (soft) | - | - |

**Search Query Parameters:**
- `title` (optional): Search by book title
- `author` (optional): Filter by author name (mapped to `Author` filter in `SearchBookRequest`)
- `category` (optional): Filter by category name
- `minPrice` (optional): Minimum price filter
- `maxPrice` (optional): Maximum price filter
- Pagination: `page`, `size`, `sort` (default: 20 items per page, sorted by title)

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

### Caching & Performance
- **Spring Cache** - Caching abstraction
- **Redis** - Distributed cache store (categories: 7 days, books: 30 min)

### File Storage
- **Cloudinary** - Cloud-based image storage and CDN

### API Documentation
- **SpringDoc OpenAPI 3.0.1** - Auto-generated API documentation (Swagger UI)

### Development & DevOps
- **Spring Boot DevTools** - Hot reload
- **Spring Boot Docker Compose** - Container orchestration

---

## 🐳 Docker Configuration

### Docker Compose Services

```yaml
services:
  
  mysql-db:               # Database
    - Image: mysql:8.0
    - Container: bookstore-db
    - Port: 3306
    - Database: bookstore
    - Credentials: root/123456
    - Volume: mysql_data (persistent storage)
  
  redis:                  # Cache & Session Store
    - Image: redis:latest
    - Container: bookstore-redis
    - Port: 6379
    - Persistence: Saves every 60 seconds if at least 1 change
    - Volume: redis_data
    - Auto-restart: Always
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

### Redis Cache Configuration
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.cache.type=redis
# Cache TTLs:
# - categories: 7 days
# - books: 30 minutes
# - books_page: 30 minutes
# - default: 60 minutes
```

### Cloudinary Configuration
```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
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
| 1 | `001-create-category-table.yaml` | Creates `categories` table with soft delete |
| 2 | `002-create-book-table.yaml` | Creates `books` table with soft delete |
| 3 | `003-create-book-category-table.yaml` | Creates `book_category` join table (Many-to-Many) |
| 4 | `004-insert-sample-datas.yaml` | Inserts sample books and categories data |
| 5 | `005-create-role-table.yaml` | Creates `roles` table |
| 6 | `006-create-user-table.yaml` | Creates `users` table with auth fields |
| 7 | `007-create-user-role-table.yaml` | Creates `user_roles` join table (Many-to-Many) |
| 8 | `008-insert-roles-data.yaml` | Inserts default roles: ROLE_USER, ROLE_ADMIN |
| 9 | `009-create-order-table.yaml` | Creates `orders` table with status enum |
| 10 | `010-create-order-item-table.yaml` | Creates `order_items` table |
| 11 | `011-create-cart-and-cartitem-tables.yaml` | Creates `carts` and `cart_items` tables |
| 12 | `012-create-refresh-token-table.yaml` | Creates `refresh_tokens` table |
| 13 | `013-create-oauth2-account-table.yaml` | Creates `oauth2_users` for OAuth2 integration |
| 14 | `014-update-user-table.yaml` | Updates user table structure |
| 15 | `015-create-author-table.yaml` | Creates `authors` table and `book_author` join table |
| 16 | `016-add-author-id-to-books.yaml` | Creates `book_author` join table with indexes and foreign keys |
| 17 | `017-backfill-authors-from-books.yaml` | Backfills author data into author relations |
| 18 | `018-enforce-author-id-not-null.yaml` | Enforces complete `book_author` mapping for books with legacy author data |

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
  - include: { file: db/changelog/changes/001-create-category-table.yaml }
  - include: { file: db/changelog/changes/002-create-book-table.yaml }
  - include: { file: db/changelog/changes/003-create-book-category-table.yaml }
  - include: { file: db/changelog/changes/004-insert-sample-datas.yaml }
  - include: { file: db/changelog/changes/005-create-role-table.yaml }
  - include: { file: db/changelog/changes/006-create-user-table.yaml }
  - include: { file: db/changelog/changes/007-create-user-role-table.yaml }
  - include: { file: db/changelog/changes/008-insert-roles-data.yaml }
  - include: { file: db/changelog/changes/009-create-order-table.yaml }
  - include: { file: db/changelog/changes/010-create-order-item-table.yaml }
  - include: { file: db/changelog/changes/011-create-cart-and-cartitem-tables.yaml }
  - include: { file: db/changelog/changes/012-create-refresh-token-table.yaml }
  - include: { file: db/changelog/changes/013-create-oauth2-account-table.yaml }
  - include: { file: db/changelog/changes/014-update-user-table.yaml }
  - include: { file: db/changelog/changes/015-create-author-table.yaml }
  - include: { file: db/changelog/changes/016-add-author-id-to-books.yaml }
  - include: { file: db/changelog/changes/017-backfill-authors-from-books.yaml }
  - include: { file: db/changelog/changes/018-enforce-author-id-not-null.yaml }
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
   Body: BookRequest (title, authorsIds, price, categoryIds)
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

### 🛡️ Error Handling

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    → HTTP 404 NOT_FOUND
    
    @ExceptionHandler(DuplicateResourceException.class)
    → HTTP 409 CONFLICT
    
    @ExceptionHandler(UnauthorizedException.class)
    → HTTP 401 UNAUTHORIZED
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    → HTTP 400 BAD_REQUEST (validation errors)
    
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

### Test Structure (Recommended)
```
src/test/java/
├── controller/    # Integration tests for controllers
├── service/       # Unit tests for services
├── repository/    # Repository tests
└── security/      # Security configuration tests
```

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
- ✅ Browse books (public, paginated with default size=5)
- ✅ **Search books** (by title, author, category, price range)
- ✅ View book details (public)
- ✅ Create books (ADMIN only)
- ✅ Update books (ADMIN only)
- ✅ Soft delete books (ADMIN only)
- ✅ **JPA Specification** for dynamic query building
- ✅ Many-to-Many relationship with categories
- ✅ ISBN, cover image, quantity tracking
- ✅ **Redis caching** with 30-minute TTL
- ✅ EntityGraph optimization for category loading

### 📑 Category Management
- ✅ Browse categories (public)
- ✅ View category details (public)
- ✅ Create categories (ADMIN only)
- ✅ Update categories (ADMIN only)
- ✅ Soft delete categories (ADMIN only)
- ✅ **Redis caching** with 7-day TTL
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

### 📤 File Upload
- ✅ **Cloudinary integration** for image uploads
- ✅ Upload endpoint returns CDN URL
- ✅ Support for book cover images
- ✅ Cloud-based storage with CDN delivery

### 🗄️ Database & Persistence
- ✅ MySQL 8.0 database
- ✅ Liquibase migrations (18 changesets)
- ✅ 12 JPA entities with relationships
- ✅ 11 repositories
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
- ✅ Custom exceptions (ResourceNotFoundException, DuplicateResourceException, UnauthorizedException)
- ✅ Docker Compose integration (MySQL + Redis)
- ✅ **Redis caching** with configurable TTLs
- ✅ **Cloudinary file storage**
- ✅ **OpenAPI/Swagger documentation**
- ✅ Spring Boot DevTools
- ✅ Pagination support (Spring Data Pageable)
- ✅ **PageResponse DTO** for standardized pagination
- ✅ **BookSpecification** for dynamic search queries

---

## 🔮 Future Enhancements (Potential)

- [x] ~~Role-based access control (RBAC)~~ ✅ **Implemented**
- [x] ~~JWT refresh token mechanism~~ ✅ **Implemented**
- [x] ~~Shopping cart functionality~~ ✅ **Implemented**
- [x] ~~Order management system~~ ✅ **Implemented**
- [x] ~~OAuth2 Google integration~~ ✅ **Implemented**
- [x] ~~Pagination for lists~~ ✅ **Implemented**
- [x] Add search and filtering for books (by title, author, category, price range) ✅ **Implemented**
- [x] Implement Redis caching for frequently accessed data
- [x] Add Swagger/OpenAPI documentation ✅ **Implemented**
- [x] Implement file upload for book cover images (currently URL-based) ✅ **Implemented**
- [x] Add comprehensive unit and integration tests
- [x] Implement CI/CD pipeline (GitHub Actions)
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
**Technology Stack:** Spring Boot 4.0.4 + MySQL 8.0 + Redis + Cloudinary + JWT + OAuth2 + Swagger  
**Architecture:** Layered MVC with Spring Security  
**Status:** ✅ Production-Ready Implementation Complete

### Project Statistics
- **Controllers:** 6 (Auth, Book, Category, Cart, Order, FileUpload)
- **Services:** 8 interfaces with implementations
- **Repositories:** 10 JPA repositories
- **Entities:** 11 domain models
- **Database Tables:** 12 (including join tables)
- **Liquibase Changesets:** 14
- **API Endpoints:** 30+ RESTful endpoints
- **External Services:** Redis (caching), Cloudinary (file storage), Google OAuth2
- **Lines of Code:** Comprehensive enterprise-grade application

---

*Last Updated: April 5, 2026*
