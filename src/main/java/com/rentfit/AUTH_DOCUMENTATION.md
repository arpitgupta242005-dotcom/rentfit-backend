# RentFit Authentication Module

## Overview
The Authentication module manages email OTP verification, user registration, login, and JWT-based access to protected APIs.

## Technology Stack
- Java and Spring Boot
- Spring Security
- PostgreSQL
- JWT for stateless authentication
- Email delivery through Gmail SMTP
- Postman for API testing

## Authentication Flow
1. **Send OTP:** The client requests an OTP for email verification.
2. **Verify OTP:** The client submits the received OTP for verification.
3. **Register:** The user submits registration details after email verification.
4. **Login:** The user submits login credentials and receives a JWT after successful authentication.
5. **Access protected APIs:** The client sends the token in the `Authorization` header as `Bearer <JWT>`.

## API Endpoints
The following endpoint paths are based on the current development/testing flow. Confirm the exact mappings and request fields against `AuthController` before treating this table as final.

| Operation | HTTP method | Endpoint |
|---|---|---|
| Send OTP | POST | `/api/auth/send-otp` |
| Verify OTP | POST | `/api/auth/verify-otp` |
| Register | POST | `/api/auth/register` |
| Login | POST | `/api/auth/login` |

Request fields and response bodies depend on the corresponding DTOs and controller implementation. Use the actual DTO fields when making requests in Postman.

## JWT Security
- The application uses Spring Security with stateless sessions.
- Authentication endpoints under `/api/auth/**` are permitted without an existing JWT.
- Other endpoints require authentication.
- Include the token in requests to protected endpoints:

```http
Authorization: Bearer <JWT>
```

## Testing Summary
Basic tests performed during development:
- OTP send and verification: successful.
- Registration: successful; duplicate registration returned `409 Conflict`; registration with an unverified email returned `400 Bad Request`.
- Login: successful and returned a JWT.
- Protected API `GET /api/test/protected`:
  - Valid JWT: `200 OK`
  - Missing or invalid JWT: `403 Forbidden`

## Configuration and Secrets
Configure database, mail, and JWT settings through environment-specific configuration. Do not commit `.env`, database passwords, Gmail App Passwords, JWT secrets, or real OTPs to GitHub.

## Notes
This document summarizes the current development implementation and basic manual tests. Before release, verify endpoint mappings, DTO fields, response examples, and status codes against the source code and add automated tests as appropriate.
