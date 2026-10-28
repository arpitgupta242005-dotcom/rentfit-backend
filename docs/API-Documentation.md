# RentFit - API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
Currently, the API does not require authentication for OTP endpoints. JWT-based authentication will be implemented in future iterations.

## Common Response Format

### Success Response
```json
{
  "message": "Success message"
}
```

### Error Response
```json
{
  "message": "Error message",
  "errors": [
    {
      "field": "email",
      "message": "Please provide a valid email address"
    }
  ]
}
```

## Endpoints

### Authentication Endpoints

#### 1. Send OTP

Initiates the email verification process by sending a 6-digit OTP to the provided email address.

**Endpoint**: `POST /auth/send-otp`

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Request Fields**:
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| email | string | Yes | Must be a valid email format |

**Success Response** (200 OK):
```json
{
  "message": "OTP sent successfully"
}
```

**Error Responses**:
- **400 Bad Request**: Invalid email format
  ```json
  {
    "message": "Validation failed",
    "errors": [
      {
        "field": "email",
        "message": "Please provide a valid email address"
      }
    ]
  }
  ```

- **500 Internal Server Error**: Email sending failed
  ```json
  {
    "message": "Unable to send OTP email"
  }
  ```

**Notes**:
- OTP expires in 5 minutes
- Only one OTP per email is stored at a time (new OTPs replace old ones)
- Email is case-insensitive (stored in lowercase)

---

#### 2. Verify OTP

Verifies the OTP code sent to the user's email address.

**Endpoint**: `POST /auth/verify-otp`

**Request Body**:
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Request Fields**:
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| email | string | Yes | Must be a valid email format |
| otp | string | Yes | Must be exactly 6 digits (0-9) |

**Success Response** (200 OK):
```json
{
  "message": "OTP verified successfully"
}
```

**Error Responses**:
- **400 Bad Request**: Invalid or expired OTP
  ```json
  {
    "message": "Invalid or expired OTP"
  }
  ```

- **400 Bad Request**: Validation errors
  ```json
  {
    "message": "Validation failed",
    "errors": [
      {
        "field": "otp",
        "message": "OTP must be exactly 6 digits"
      }
    ]
  }
  ```

**Notes**:
- OTP is invalidated after successful verification
- Expired OTPs are automatically removed
- Failed verification attempts do not consume the OTP (allows retry)

---

### Planned Endpoints

#### 3. Register User

Register a new user account with email verification.

**Endpoint**: `POST /auth/register`

**Request Body**:
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "SecurePass123",
  "phone": "+1234567890"
}
```

**Request Fields**:
| Field | Type | Required | Validation |
|-------|------|----------|------------|
| name | string | Yes | Not blank |
| email | string | Yes | Valid email, unique |
| password | string | Yes | Min 8 characters |
| phone | string | No | Valid phone format |

**Success Response** (201 Created):
```json
{
  "message": "User registered successfully",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "USER",
    "status": "ACTIVE",
    "isVerified": false
  }
}
```

---

#### 4. Login

Authenticate user and receive access token.

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "email": "john@example.com",
  "password": "SecurePass123"
}
```

**Success Response** (200 OK):
```json
{
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "user": {
      "id": 1,
      "name": "John Doe",
      "email": "john@example.com",
      "role": "USER"
    }
  }
}
```

---

#### 5. Get User Profile

Retrieve user profile information.

**Endpoint**: `GET /users/{id}`

**Headers**:
```
Authorization: Bearer {token}
```

**Success Response** (200 OK):
```json
{
  "message": "User profile retrieved",
  "data": {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "phone": "+1234567890",
    "role": "USER",
    "status": "ACTIVE",
    "isVerified": true,
    "businessName": null,
    "profileImageUrl": "https://example.com/images/user1.jpg",
    "lastSeenAt": "2026-10-27T10:30:00",
    "createdAt": "2026-10-01T08:00:00"
  }
}
```

---

#### 6. Update User Profile

Update user profile information.

**Endpoint**: `PUT /users/{id}`

**Headers**:
```
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "name": "John Smith",
  "phone": "+9876543210",
  "businessName": "John's Rentals"
}
```

**Success Response** (200 OK):
```json
{
  "message": "Profile updated successfully",
  "data": {
    "id": 1,
    "name": "John Smith",
    "email": "john@example.com",
    "phone": "+9876543210",
    "businessName": "John's Rentals"
  }
}
```

---

#### 7. Upload Profile Image

Upload user profile image.

**Endpoint**: `POST /users/{id}/upload-image`

**Headers**:
```
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request Body**:
```
image: [binary file]
```

**Success Response** (200 OK):
```json
{
  "message": "Image uploaded successfully",
  "data": {
    "profileImageUrl": "https://example.com/images/user1-updated.jpg"
  }
}
```

---

## HTTP Status Codes

| Code | Description |
|------|-------------|
| 200 | OK - Request successful |
| 201 | Created - Resource created successfully |
| 400 | Bad Request - Invalid input or validation error |
| 401 | Unauthorized - Authentication required or failed |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists (e.g., duplicate email) |
| 500 | Internal Server Error - Server error |

## Error Codes

| Error Code | Description |
|------------|-------------|
| VALIDATION_ERROR | Request validation failed |
| INVALID_OTP | OTP is invalid or expired |
| EMAIL_SEND_FAILED | Failed to send email |
| USER_NOT_FOUND | User does not exist |
| DUPLICATE_EMAIL | Email already registered |
| UNAUTHORIZED | Authentication required |
| FORBIDDEN | Insufficient permissions |

## Rate Limiting

Planned rate limits:
- Send OTP: 3 requests per 10 minutes per email
- Verify OTP: 10 requests per minute per email
- Login: 5 attempts per 15 minutes per IP

## CORS Configuration

Allow requests from:
- `http://localhost:3000` (development frontend)
- Production frontend URL (to be configured)

## Pagination

For list endpoints, use query parameters:
```
GET /items?page=0&size=20&sort=name,asc
```

- `page`: Page number (0-indexed)
- `size`: Items per page (default: 20, max: 100)
- `sort`: Sort field and direction (field,direction)

## Versioning

API versioning will be implemented via URL path:
- Current: `/api`
- Future: `/api/v1`, `/api/v2`

## Testing the API

### Using cURL

Send OTP:
```bash
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com"}'
```

Verify OTP:
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","otp":"123456"}'
```

### Using Postman

1. Import the API collection (to be provided)
2. Set base URL to `http://localhost:8080/api`
3. Configure environment variables for authentication tokens

## WebSocket Endpoints (Planned)

Real-time notifications will be implemented via WebSocket:
- Connection: `ws://localhost:8080/ws`
- Events: rental_request, rental_status, payment_update

## SDKs

Client SDKs will be provided for:
- JavaScript/TypeScript
- Python
- Java
