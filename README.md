# Auth Spring API

A modern Spring Boot authentication API with JWT tokens, built with the latest versions of Spring Boot 3.x and Java 21.

## Features

- **JWT Authentication**: Secure token-based authentication
- **User Registration & Login**: Complete user management
- **Password Encryption**: BCrypt password hashing
- **CORS Support**: Configured for frontend integration
- **H2 Database**: In-memory database for development
- **PostgreSQL Ready**: Production database configuration included
- **Modern Java**: Built with Java 21 and Spring Boot 3.2.1

## Tech Stack

- **Java 21**
- **Spring Boot 3.2.1**
- **Spring Security 6**
- **JWT (JSON Web Tokens)**
- **Spring Data JPA**
- **H2 Database** (development)
- **PostgreSQL** (production ready)
- **Maven** (build tool)

## API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login user |
| POST | `/api/auth/refresh` | Refresh access token |
| GET | `/api/auth/me` | Get current user profile |
| POST | `/api/auth/logout` | Logout user |

### Request/Response Examples

#### Register User
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123"
}
```

#### Login User
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "johndoe",
  "password": "password123"
}
```

#### Response Format
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "token_type": "Bearer",
    "expires_in": 1800000,
    "user": {
      "id": 1,
      "username": "johndoe",
      "email": "john@example.com",
      "created_at": "2025-01-01T10:00:00",
      "role": "USER"
    }
  }
}
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+

### Running the Application

1. **Clone the repository** (if needed)
2. **Navigate to the project directory**:
   ```bash
   cd auth-spring-api
   ```

3. **Run with Maven**:
   ```bash
   mvn spring-boot:run
   ```

4. **Or build and run the JAR**:
   ```bash
   mvn clean package
   java -jar target/auth-spring-api-0.0.1-SNAPSHOT.jar
   ```

The application will start on `http://localhost:8080`

### Configuration

The application is configured via `src/main/resources/application.yml`:

- **Server Port**: 8080
- **Database**: H2 (in-memory for development)
- **JWT Secret**: Base64 encoded secret
- **CORS**: Configured for localhost:3000 and Vercel deployment

### Database

#### Development (H2)
- **Console**: http://localhost:8080/h2-console
- **JDBC URL**: jdbc:h2:mem:testdb
- **Username**: sa
- **Password**: password

#### Production (PostgreSQL)
Update `application.yml` with your PostgreSQL configuration:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/authdb
    username: your_username
    password: your_password
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
```

## Integration with Frontend

This API is designed to work with your Next.js frontend. Update your frontend API calls to point to:

- Development: `http://localhost:8080/api/auth`
- Production: Your deployed Spring Boot URL

### Environment Variables for Frontend

```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api/auth
# or for production
NEXT_PUBLIC_API_URL=https://your-spring-api-url.com/api/auth
```

## Security Features

- **JWT Tokens**: Secure, stateless authentication
- **Password Hashing**: BCrypt encryption
- **CORS Configuration**: Proper cross-origin setup
- **Request Validation**: Input validation on all endpoints
- **Error Handling**: Consistent error responses

## Development Tools

- **Spring Boot DevTools**: Hot reload during development
- **H2 Console**: Database inspection at `/h2-console`
- **Logging**: Debug logging for security and application events

## Testing

Run tests with Maven:
```bash
mvn test
```

## Production Deployment

1. **Build the application**:
   ```bash
   mvn clean package
   ```

2. **Deploy the JAR file** to your server
3. **Configure PostgreSQL** database
4. **Update CORS origins** for your production frontend URL
5. **Set secure JWT secret** via environment variables

## Environment Variables

For production, set these environment variables:

```bash
JWT_SECRET=your-secure-base64-encoded-secret
SPRING_DATASOURCE_URL=your-database-url
SPRING_DATASOURCE_USERNAME=your-db-username
SPRING_DATASOURCE_PASSWORD=your-db-password
CORS_ALLOWED_ORIGINS=https://your-frontend-url.com
```

## License

This project is licensed under the MIT License.
