# Spring Boot Auth API - Converted from Next.js

This Spring Boot application has been converted from the Next.js authentication API and provides the following endpoints:

## API Endpoints

### 1. POST /api/auth/login
- **Purpose**: Authenticate user and create session
- **Body**: `{"email": "user@example.com", "password": "password123"}`
- **Response**: `{"message": "Login successful", "user": {"id": 1, "email": "user@example.com"}}`
- **Cookie**: Sets HTTP-only `access_token` cookie with JWT containing session information

### 2. POST /api/auth/logout  
- **Purpose**: Clear authentication session
- **Response**: `{"message": "Logout successful"}`
- **Cookie**: Clears the `access_token` cookie

### 3. GET /api/auth/verify
- **Purpose**: Verify current session and return user information
- **Response**: `{"status": "ok", "user": {"id": 1, "email": "user@example.com"}, "session": {"timeRemaining": 118, "expiresAt": 1694609832}}`
- **Error Response**: `{"error": "Session expired"}` or `{"error": "Access token not found"}`

## Mock Users (Runtime Database)
The application initializes with these mock users:
- `user@example.com` / `password123`
- `admin@example.com` / `admin123`

## Session Configuration  
- Default session lifetime: 120 seconds (2 minutes)
- Configurable via `session.lifetime` property
- JWT tokens include session start time to enforce session expiry
- Session expiry is calculated based on start time + lifetime, not token expiry

## Features Matching Next.js Implementation
- ✅ Email-based authentication (not username)  
- ✅ HTTP-only cookies for session management
- ✅ Session-based expiry with configurable lifetime  
- ✅ Mock users in runtime database
- ✅ Session start time tracking in JWT
- ✅ Time remaining calculation
- ✅ CORS support for frontend integration
- ✅ Error handling matching Next.js responses

## Quick Test Commands
```bash
# Start the application
mvn spring-boot:run

# Test login (in another terminal)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}' \
  -c cookies.txt -v

# Test verify (uses cookie from login)  
curl -X GET http://localhost:8080/api/auth/verify \
  -b cookies.txt -v

# Test logout
curl -X POST http://localhost:8080/api/auth/logout \
  -b cookies.txt -v
```

## Technology Stack
- Spring Boot 3.2.1
- Spring Security (simplified, no JWT filter chains)
- Spring Data JPA with H2 in-memory database
- JWT with jose4j for token generation
- Plain text password storage (for mock data only)
- Maven build system
