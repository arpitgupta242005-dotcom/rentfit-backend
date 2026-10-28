# RentFit - System Architecture

## 1. Overview

RentFit is a Spring Boot-based REST API backend following a layered architecture pattern. The system is designed to be scalable, maintainable, and secure, with clear separation of concerns across different layers.

## 2. Architecture Pattern

### 2.1 Layered Architecture

The application follows a classic 3-tier layered architecture:

```
┌─────────────────────────────────────┐
│         Presentation Layer          │
│      (Controllers / REST API)       │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│          Business Layer             │
│          (Services)                 │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│         Data Access Layer           │
│      (Repositories / JPA)           │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│          Database Layer             │
│         (PostgreSQL)                │
└─────────────────────────────────────┘
```

## 3. Technology Stack

### 3.1 Core Technologies
- **Java 17**: Programming language
- **Spring Boot 4.1.2**: Application framework
- **Maven**: Dependency management and build tool
- **PostgreSQL**: Relational database
- **Spring Data JPA**: ORM and data access
- **Hibernate**: JPA implementation

### 3.2 Supporting Libraries
- **Lombok**: Boilerplate code reduction
- **Spring Validation**: Input validation
- **Spring Mail**: Email sending capabilities
- **Spring Boot Test**: Testing framework

## 4. Package Structure

```
com.rentfit/
├── RentfitApplication.java          # Main application entry point
├── controller/                       # REST API controllers
│   └── AuthController.java          # Authentication endpoints
├── service/                          # Business logic layer
│   └── OtpService.java              # OTP generation and verification
├── repository/                       # Data access layer
│   └── UserRepository.java          # User data operations
├── entity/                           # JPA entities
│   └── User.java                    # User domain model
├── dto/                              # Data Transfer Objects
│   ├── RegisterRequest.java         # Registration request
│   ├── SendOtpRequest.java          # OTP send request
│   └── VerifyOtpRequest.java        # OTP verification request
└── enums/                            # Enumerations
    ├── Role.java                    # User roles
    └── UserStatus.java              # User status values
```

## 5. Component Details

### 5.1 Presentation Layer (Controllers)

**Purpose**: Handle HTTP requests, validate input, delegate to services, and return responses.

**AuthController**:
- Manages authentication-related endpoints
- Validates request DTOs using Spring Validation
- Delegates business logic to OtpService
- Returns standardized JSON responses

**Key Responsibilities**:
- Request validation
- Response formatting
- HTTP status code management
- Exception handling (via global exception handler - to be added)

### 5.2 Business Layer (Services)

**Purpose**: Implement business logic, coordinate data operations, and maintain application state.

**OtpService**:
- Generates secure 6-digit OTPs using SecureRandom
- Stores OTPs in memory with expiration (5 minutes)
- Sends OTP emails via JavaMailSender
- Verifies OTPs against stored values
- Handles OTP expiration and cleanup

**Key Features**:
- Thread-safe OTP storage using ConcurrentHashMap
- In-memory storage for performance (consider Redis for production)
- 5-minute OTP expiration
- Email sending with error handling
- Automatic OTP cleanup after verification

### 5.3 Data Access Layer (Repositories)

**Purpose**: Abstract database operations using Spring Data JPA.

**UserRepository**:
- Extends JpaRepository for CRUD operations
- Custom query methods for email lookup
- Email existence check

**Key Methods**:
- `findByEmail(String email)`: Find user by email
- `existsByEmail(String email)`: Check email uniqueness

### 5.4 Entity Layer

**Purpose**: Define database schema and domain models.

**User Entity**:
- Maps to `users` table
- Uses Lombok for getters/setters
- Enum storage as STRING
- Automatic timestamp management

**Key Annotations**:
- `@Entity`: JPA entity marker
- `@Table`: Table name mapping
- `@Id`: Primary key
- `@GeneratedValue`: Auto-increment strategy
- `@Enumerated(EnumType.STRING)`: Enum storage
- `@CreationTimestamp`: Auto-generated timestamps

### 5.5 DTO Layer

**Purpose**: Define API request/response contracts with validation.

**RegisterRequest**:
- User registration data
- Validation: name required, valid email, password min 8 chars

**SendOtpRequest**:
- OTP request data
- Validation: valid email required

**VerifyOtpRequest**:
- OTP verification data
- Validation: valid email, 6-digit numeric OTP

## 6. Data Flow

### 6.1 OTP Send Flow

```
Client Request
    ↓
AuthController.sendOtp()
    ↓ (validation)
OtpService.sendOtp()
    ↓ (generate OTP)
ConcurrentHashMap (in-memory storage)
    ↓ (send email)
JavaMailSender
    ↓
Email Service (SMTP)
    ↓
Client Email
```

### 6.2 OTP Verification Flow

```
Client Request
    ↓
AuthController.verifyOtp()
    ↓ (validation)
OtpService.verifyOtp()
    ↓ (lookup OTP)
ConcurrentHashMap
    ↓ (check expiry & match)
Boolean Result
    ↓
Response to Client
```

## 7. Database Schema

### 7.1 Users Table

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR NOT NULL,
    email VARCHAR NOT NULL UNIQUE,
    password_hash VARCHAR NOT NULL,
    phone VARCHAR,
    role VARCHAR NOT NULL,  -- 'USER' or 'ADMIN'
    status VARCHAR NOT NULL, -- 'ACTIVE' or 'BLOCKED'
    is_verified BOOLEAN DEFAULT FALSE,
    business_name VARCHAR,
    profile_image_url VARCHAR,
    last_seen_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL
);
```

### 7.2 Indexes
- Unique index on `email` for fast lookups and uniqueness enforcement
- Index on `status` for filtering active users (to be added)
- Index on `role` for admin queries (to be added)

## 8. Security Architecture

### 8.1 Current Security Measures
- Password hashing (using BCrypt - to be implemented)
- Email validation
- OTP-based verification
- Input validation on all endpoints

### 8.2 Planned Security Enhancements
- JWT token-based authentication
- Spring Security integration
- Role-based access control (RBAC)
- Rate limiting on sensitive endpoints
- CSRF protection
- HTTPS enforcement
- SQL injection prevention (via JPA parameterized queries)

## 9. Scalability Considerations

### 9.1 Current Limitations
- In-memory OTP storage (not suitable for horizontal scaling)
- No caching layer
- No connection pooling configuration visible

### 9.2 Recommended Improvements
- **Redis**: Replace in-memory OTP storage with Redis for distributed caching
- **Database Connection Pooling**: Configure HikariCP for efficient connection management
- **Load Balancing**: Deploy behind load balancer for horizontal scaling
- **Database Replication**: Implement read replicas for read-heavy operations
- **CDN**: Serve static assets (profile images) via CDN

## 10. Deployment Architecture

### 10.1 Development Environment
- Local PostgreSQL database
- Mail server configuration via application.properties
- Maven for building and running

### 10.2 Production Architecture (Recommended)

```
┌─────────────────────────────────────┐
│         Load Balancer               │
│       (Nginx / AWS ALB)            │
└─────────────────────────────────────┘
                 ↓
    ┌───────────┴───────────┐
    ↓                       ↓
┌─────────────┐      ┌─────────────┐
│  Spring     │      │  Spring     │
│  Boot App 1 │      │  Boot App 2 │
└─────────────┘      └─────────────┘
    ↓                       ↓
┌─────────────────────────────────────┐
│         PostgreSQL Database         │
│      (Primary + Read Replicas)     │
└─────────────────────────────────────┘
                 ↓
┌─────────────────────────────────────┐
│         Redis Cache                 │
│      (OTP storage, session data)    │
└─────────────────────────────────────┘
```

## 11. API Design Principles

### 11.1 RESTful Conventions
- Resource-based URLs
- HTTP method semantics (GET, POST, PUT, DELETE)
- Standard HTTP status codes
- JSON request/response format
- Versioning via URL path (e.g., /api/v1/)

### 11.2 Response Format
```json
{
  "message": "Success message",
  "data": { ... }
}
```

### 11.3 Error Format
```json
{
  "message": "Error message",
  "errors": [ ... ]
}
```

## 12. Logging Strategy

### 12.1 Current State
- Spring Boot default logging
- No custom logging configuration visible

### 12.2 Recommended Approach
- Structured logging (JSON format)
- Log levels: ERROR, WARN, INFO, DEBUG
- Request ID tracking for distributed tracing
- Sensitive data redaction (passwords, OTPs)
- Log aggregation (ELK stack or CloudWatch)

## 13. Testing Strategy

### 13.1 Unit Testing
- Service layer logic testing
- Repository layer with H2 in-memory database
- DTO validation testing

### 13.2 Integration Testing
- Controller endpoint testing
- Database integration testing
- Email service mocking

### 13.3 End-to-End Testing
- Complete user flows
- API contract testing
- Performance testing

## 14. Monitoring & Observability

### 14.1 Metrics to Track
- API response times
- Error rates
- OTP generation/verification rates
- Database query performance
- Memory usage
- CPU utilization

### 14.2 Recommended Tools
- Spring Boot Actuator
- Prometheus for metrics collection
- Grafana for visualization
- Application Performance Monitoring (APM) tools

## 15. Future Architecture Enhancements

### 15.1 Microservices Transition
When the application grows, consider splitting into:
- Authentication Service
- User Service
- Item Service
- Rental Service
- Payment Service
- Notification Service

### 15.2 Event-Driven Architecture
- Implement message broker (RabbitMQ/Kafka)
- Event sourcing for audit trail
- Async processing for email notifications

### 15.3 API Gateway
- Centralized routing
- Rate limiting
- Authentication/authorization
- Request/response transformation
