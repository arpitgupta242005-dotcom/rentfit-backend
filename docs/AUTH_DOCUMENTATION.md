# Authentication API Documentation

## Overview
JWT-based authentication with email OTP verification. All endpoints return standardized `ApiResponse` wrapper.

## Authentication Flow
1. Send OTP to email
2. Verify OTP
3. Register user (requires verified email)
4. Login to get JWT token
5. Use JWT in `Authorization: Bearer <token>` header for protected endpoints

## API Endpoints

### 1. Send OTP
**POST** `/api/auth/send-otp`

**Request:**
```json
{
  "email": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "OTP sent successfully",
  "data": null,
  "status": 200
}
```

---

### 2. Verify OTP
**POST** `/api/auth/verify-otp`

**Request:**
```json
{
  "email": "user@example.com",
  "otp": "123456"
}
```

**Response (200 OK):**
```json
{
  "message": "OTP verified successfully",
  "data": null,
  "status": 200
}
```

**Response (400 Bad Request - Invalid OTP):**
```json
{
  "message": "Invalid or expired OTP",
  "data": null,
  "status": 400
}
```

---

### 3. Register
**POST** `/api/auth/register`

**Request:**
```json
{
  "name": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (201 Created):**
```json
{
  "message": "User registered successfully.",
  "data": {
    "message": "Registration successful",
    "id": 1,
    "email": "user@example.com"
  },
  "status": 201
}
```

**Response (400 Bad Request - Email not verified):**
```json
{
  "message": "Please verify your email using OTP first",
  "data": null,
  "status": 400
}
```

**Response (409 Conflict - Email exists):**
```json
{
  "message": "user with email user@example.com already exists",
  "data": null,
  "status": 409
}
```

---

### 4. Login
**POST** `/api/auth/login`

**Request:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 1,
    "name": "John Doe",
    "email": "user@example.com",
    "role": "USER"
  },
  "status": 200
}
```

**Response (401 Unauthorized - Invalid credentials):**
```json
{
  "message": "Invalid login credentials",
  "data": null,
  "status": 401
}
```

---

## Authorization

### Role-Based Access Control
- **USER**: Can access `/api/user/**` endpoints
- **ADMIN**: Can access `/api/admin/**` and `/api/user/**` endpoints

### Protected Endpoints
Include JWT token in Authorization header:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Example Request:**
```http
GET /api/user/profile
Authorization: Bearer <JWT_TOKEN>
```

**Response (401 Unauthorized - Missing/Invalid Token):**
```json
{
  "message": "Authentication failed",
  "data": null,
  "status": 401
}
```

---

## Response Format
All endpoints return standardized `ApiResponse`:

```json
{
  "message": "Descriptive message",
  "data": <response data or null>,
  "status": <HTTP status code>
}
```

Error responses use `ApiErrorResponse` with additional error details when applicable.

---

## Security Notes
- JWT expires in 1 hour (configurable)
- OTP expires in 5 minutes
- Email verification expires in 10 minutes
- All auth endpoints are public
- All other endpoints require valid JWT
- User account status must be ACTIVE for authentication
