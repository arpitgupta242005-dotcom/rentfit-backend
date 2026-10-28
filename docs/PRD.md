# RentFit - Product Requirements Document

## 1. Executive Summary

RentFit is a rental platform backend system that enables users to rent items, manage listings, and process transactions securely. The system provides role-based access control for regular users and administrators, with email-based authentication and verification.

## 2. Product Vision

To create a secure, scalable, and user-friendly rental platform backend that supports item listings, user management, rental transactions, and administrative oversight.

## 3. Target Users

### 3.1 End Users (Customers)
- Individuals looking to rent items
- Users browsing available rentals
- Customers managing their rental requests

### 3.2 Business Users (Renters)
- Individuals or businesses listing items for rent
- Managing their inventory and availability
- Processing rental requests

### 3.3 Administrators
- Platform managers overseeing operations
- Moderating content and user accounts
- Handling disputes and platform-wide configurations

## 4. Core Features

### 4.1 Authentication & Authorization
- Email-based registration
- OTP verification for email validation
- Role-based access control (USER, ADMIN)
- Session management
- Password security (hashed storage)

### 4.2 User Management
- User profile creation and management
- Profile image upload
- Account status management (ACTIVE, BLOCKED)
- Last seen tracking
- Business account support (business name)

### 4.3 Item Management (Planned)
- Create rental listings
- Update item details
- Manage item availability
- Delete listings
- Category management

### 4.4 Rental Operations (Planned)
- Browse available items
- Submit rental requests
- Accept/reject requests
- Track rental status
- Return process

### 4.5 Transaction Management (Planned)
- Payment processing
- Rental period management
- Security deposits
- Fee calculation

### 4.6 Administrative Features (Planned)
- User account moderation
- Content moderation
- Platform analytics
- Dispute resolution
- System configuration

## 5. Functional Requirements

### 5.1 Authentication Module
- **FR-1.1**: System shall allow users to register with name,email and password
- **FR-1.2**: System shall send 6-digit OTP to user's email for verification
- **FR-1.3**: OTP shall expire after 5 minutes
- **FR-1.4**: System shall verify OTP and mark user as verified
- **FR-1.5**: System shall prevent registration with duplicate email addresses
- **FR-1.6**: Passwords shall be minimum 8 characters
- **FR-1.7**: Passwords shall be hashed before storage

### 5.2 User Module
- **FR-2.1**: System shall store user profile information (name, email, phone)
- **FR-2.2**: System shall support profile image uploads
- **FR-2.3**: System shall track user last seen timestamp
- **FR-2.4**: System shall support business account type with business name
- **FR-2.5**: Admins shall be able to block/unblock user accounts
- **FR-2.6**: System shall prevent blocked users from accessing platform features

### 5.3 OTP Service
- **FR-3.1**: System shall generate secure random 6-digit OTPs
- **FR-3.2**: System shall send OTP via email
- **FR-3.3**: System shall store OTPs with expiration time
- **FR-3.4**: System shall invalidate OTP after successful verification
- **FR-3.5**: System shall reject expired OTPs
- **FR-3.6**: System shall reject invalid OTPs

## 6. Non-Functional Requirements

### 6.1 Performance
- API response time < 200ms for authentication operations
- Support for 1000+ concurrent users
- OTP email delivery within 10 seconds

### 6.2 Security
- All passwords hashed using bcrypt or similar
- HTTPS/TLS encryption for all communications
- SQL injection prevention
- XSS protection
- CSRF protection
- Rate limiting on OTP endpoints

### 6.3 Scalability
- Horizontal scaling capability
- Database connection pooling
- Stateless API design
- Caching strategy for frequently accessed data

### 6.4 Reliability
- 99.9% uptime target
- Graceful error handling
- Comprehensive logging
- Database backup strategy

### 6.5 Maintainability
- Clean code architecture
- Comprehensive test coverage (>80%)
- API documentation
- Code comments for complex logic

## 7. Technical Stack

### 7.1 Backend
- **Framework**: Spring Boot 4.1.2
- **Language**: Java 17
- **Build Tool**: Maven
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate

### 7.2 Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- Spring Boot Starter Mail
- PostgreSQL Driver
- Lombok
- Spring Boot Test

## 8. Data Model

### 8.1 User Entity
- `id`: Primary key
- `name`: User's full name
- `email`: Unique email address
- `passwordHash`: Hashed password
- `phone`: Phone number (optional)
- `role`: USER or ADMIN
- `status`: ACTIVE or BLOCKED
- `isVerified`: Email verification status
- `businessName`: Business account name (optional)
- `profileImageUrl`: Profile image URL (optional)
- `lastSeenAt`: Last activity timestamp
- `createdAt`: Account creation timestamp

## 9. API Endpoints

### 9.1 Authentication
- `POST /api/auth/send-otp` - Send OTP to email
- `POST /api/auth/verify-otp` - Verify OTP code
- `POST /api/auth/register` - Register new user (planned)
- `POST /api/auth/login` - User login (planned)
- `POST /api/auth/logout` - User logout (planned)

### 9.2 User Management (Planned)
- `GET /api/users/{id}` - Get user profile
- `PUT /api/users/{id}` - Update user profile
- `DELETE /api/users/{id}` - Delete user account
- `POST /api/users/{id}/upload-image` - Upload profile image

## 10. Success Metrics

- User registration completion rate > 80%
- Email verification rate > 90%
- Average session duration > 5 minutes
- System uptime > 99.9%
- API error rate < 0.1%

## 11. Future Enhancements

- Two-factor authentication (2FA)
- Social login (Google, Facebook)
- Password reset functionality
- Email notification preferences
- Multi-language support
- Advanced search and filtering
- Real-time notifications (WebSocket)
- Mobile app API optimization
