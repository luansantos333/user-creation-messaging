# User Creation Messaging System

A microservices-based event-driven system demonstrating Spring Security OAuth2 Authorization Server integration with Apache Kafka messaging for user lifecycle notifications.

## Overview

This project implements a secure, distributed user management system with automated email notifications. It consists of two Spring Boot applications that communicate asynchronously via Apache Kafka:

1. **user-application**: A secure REST API for user and OAuth2 client management with an embedded OAuth2 authorization server
2. **mail-application**: A Kafka-based email notification service that handles user lifecycle events

The system is fully containerized with Docker and can be deployed using Docker Compose with PostgreSQL, Kafka, and Zookeeper.

## Key Features

- **User Creation Notifications**: Automatic email notifications sent when new users are created
- **Admin Access Grants**: Email alerts when users receive administrator permissions
- **Password Reset Mechanism**: Token-based password reset flow with email delivery
- **Event-Driven Architecture**: Asynchronous messaging using Apache Kafka for decoupled communication
- **OAuth2 Authorization Server**: Full-featured OAuth2/OIDC provider with JWT support
- **Role-Based Access Control (RBAC)**: Fine-grained security with ADMIN and USER roles
- **Microservices Design**: Independently deployable services with clear separation of concerns

## Architecture

```
┌─────────────────────────────────┐                        ┌──────────────────────────┐
│     User Application            │                        │   Mail Application       │
│                                 │                        │                          │
│  • REST API Endpoints           │   Kafka Topics         │  • Kafka Consumers       │
│  • OAuth2 Authorization Server  │                        │  • Email Service         │
│  • Spring Security & JWT        ├────user-created───────>│                          │
│  • User & Role Management       │                        │  Sends Emails:           │
│  • JPA/H2 Database             │────admin-grant────────>│  • User Creation         │
│  • Kafka Event Producer         │                        │  • Admin Access Grant    │
│                                 │────password-reset─────>│  • Password Reset        │
│                                 │                  │
└─────────────────────────────────┘                        └──────────────────────────┘
```

### Event Flow

1. **User Creation Flow**:
   - Client → POST /api/user → User Application
   - User Application → Saves user to database
   - User Application → Publishes `UserCreatedEvent` to `user-created` topic
   - Mail Application → Consumes event → Sends welcome email

2. **Admin Grant Flow**:
   - Admin → PATCH /api/user/grant/{username} → User Application
   - User Application → Updates user roles in database
   - User Application → Publishes `UserAdminAccessGrant` to `admin-grant` topic
   - Mail Application → Consumes event → Sends admin notification email

3. **Password Reset Flow**:
   - User → POST /api/user/reset/{username} → User Application
   - User Application → Generates temporary token
   - User Application → Publishes `PasswordResetEvent` to `password-reset` topic
   - Mail Application → Consumes event → Sends reset link with token
   - User → Clicks link → PUT /api/user/password → User Application validates token and updates password

## Technology Stack

### User Application
- **Spring Boot 3.5.7**
- **Java 21**
- **Spring Security** - Authentication & Authorization
- **Spring OAuth2 Authorization Server** - OAuth2/OIDC provider
- **Spring Data JPA** - Data persistence
- **H2/PostgreSQL** - Database (H2 for testing, PostgreSQL for production)
- **Spring Kafka** - Message producer
- **Lombok** - Boilerplate reduction

### Mail Application
- **Spring Boot 3.5.7**
- **Java 21**
- **Spring Kafka** - Message consumer
- **Spring Mail** - Email notifications
- **Lombok** - Boilerplate reduction

## Project Structure

```
user-creation-messaging/
├── user-application/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/userapplication/
│   │       │   ├── config/          # Security & Kafka configuration
│   │       │   ├── controller/      # REST endpoints
│   │       │   │   ├── ClientController.java
│   │       │   │   └── UserController.java
│   │       │   ├── dto/             # Data transfer objects
│   │       │   ├── entity/          # JPA entities
│   │       │   │   ├── UserEntity.java
│   │       │   │   ├── ClientEntity.java
│   │       │   │   └── RoleEntity.java
│   │       │   ├── repository/      # Data access layer
│   │       │   ├── service/         # Business logic
│   │       │   │   ├── UserService.java
│   │       │   │   ├── AuthenticationService.java
│   │       │   │   ├── KafkaProducerService.java
│   │       │   │   └── RegisteredClientService.java
│   │       │   └── UserApplication.java
│   │       └── resources/
│   │           ├── application.yml
│   │           ├── application-test.yml
│   │           └── application-hml.yml
│   └── pom.xml
│
├── mail-application/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/mailapplication/
│   │       │   ├── dto/
│   │       │   │   └── UserCreatedEvent.java
│   │       │   ├── service/
│   │       │   │   └── MailService.java
│   │       │   └── MailApplication.java
│   │       └── resources/
│   └── pom.xml
│
└── docker-compose.yml
```

## Prerequisites

### For Local Development
- **Java 21** or higher
- **Maven 3.8+**
- **Apache Kafka** (or use Docker)
- **PostgreSQL** (optional, for production profile)

### For Docker Deployment
- **Docker** 20.10+
- **Docker Compose** 2.0+

## Configuration

### User Application

The application supports multiple profiles:

#### Test Profile (default)
- Uses H2 in-memory database
- H2 console available at `/h2-console`
- Default credentials: `sa/12345`

#### Environment Variables
```bash
KAFKA_URI=localhost:9092          # Kafka bootstrap servers
CLIENT_ID=default.client-id       # OAuth2 client ID
CLIENT_SECRET=default.client-secret # OAuth2 client secret
CLIENT_NAME=default.client-name   # OAuth2 client name
REDIRECT_URI=https://www.google.com.br # OAuth2 redirect URI
TOKEN_TTL=3600                    # Token time-to-live in seconds
```

### Mail Application

Configure Kafka connection and mail server settings in `application.yml`:

```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: user-created-group
  mail:
    host: smtp.example.com
    port: 587
    username: your-email@example.com
    password: your-password
```

## Getting Started

### Option 1: Docker Compose (Recommended)

The easiest way to run the entire application stack:

#### 1. Create Environment File

Create a `.env` file in the root directory based on `.env-example`:

```bash
# Copy the example file
cp .env-example .env

# Edit .env and configure all required variables
```

**Required Environment Variables:**
- `DB_PASSWORD` - PostgreSQL database password
- `DB_USERNAME` - PostgreSQL database username
- `CLIENT_ID` - OAuth2 client ID
- `CLIENT_SECRET` - OAuth2 client secret (use a strong, random value)
- `CLIENT_NAME` - OAuth2 client name
- `MAIL_USERNAME` - SMTP username (your email address)
- `MAIL_PASSWORD` - SMTP password or app-specific password
- `MAIL_HOST` - SMTP server host (e.g., smtp.gmail.com)
- `MAIL_PORT` - SMTP server port (587 for TLS, 465 for SSL)

**Optional Variables (have defaults):**
- `DB_NAME` - Database name (default: user-messaging-db)
- `REDIRECT_URI` - OAuth2 redirect URI (default: https://www.google.com.br)
- `TOKEN_TTL` - JWT token time-to-live in seconds (default: 3600)
- `KAFKA_URI` - Kafka bootstrap servers (default: kafka:9092)
- `USER_APPLICATION_PROFILE` - Spring profile (default: prod)
- `MAIL_APPLICATION_PROFILE` - Spring profile (default: prod)

See `.env-example` for a complete template with detailed descriptions.

#### 2. Start All Services

```bash
docker-compose up -d
```

This will start:
- **PostgreSQL** (port 5432): Production database
- **Zookeeper** (internal only): Kafka dependency
- **Kafka** (internal only): Message broker
- **user-application** (port 8081): User management API with OAuth2 server
- **mail-application** (port 8082): Email notification service

#### 3. Initialize Database

After the containers are running, you need to populate the database with initial data. Connect to the PostgreSQL database and run the SQL commands from `user-application/src/main/resources/import.sql`:

```bash
# Connect to PostgreSQL
docker exec -it postgres-db psql -U java-user -d user-messaging-db

# Then copy and paste the commands from import.sql:
# - Insert users (joaozinho@gmail.com, pedrinho@gmail.com)
# - Insert roles (ROLE_ADMIN, ROLE_USER)
# - Assign roles to users
```

**Note**: The database does not auto-populate on startup when using `ddl-auto: update` in production profile. Manual initialization is required.

#### 4. Check Service Health

```bash
# Check all services are running
docker-compose ps

# Check user-app health
curl http://localhost:8081/actuator/health

# Check mail-app health
curl http://localhost:8082/actuator/health
```

#### 5. View Logs

```bash
# View all logs
docker-compose logs -f

# View specific service logs
docker-compose logs -f user-app
docker-compose logs -f mail-app
docker-compose logs -f kafka
```

#### 6. Stop Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (⚠️ deletes all data)
docker-compose down -v
```

### Option 2: Local Development

For local development with Maven:

#### 1. Start Kafka

Using Docker:
```bash
docker run -d --name kafka -p 9092:9092 \
  apache/kafka:latest
```

Or use your local Kafka installation.

#### 2. Build Applications

```bash
# Build user-application
cd user-application
mvn clean install

# Build mail-application
cd ../mail-application
mvn clean install
```

#### 3. Run Applications

Terminal 1 - User Application:
```bash
cd user-application
mvn spring-boot:run
```

Terminal 2 - Mail Application:
```bash
cd mail-application
mvn spring-boot:run
```

## Docker Deployment Details

### Docker Images

The project uses multi-stage Docker builds for optimized image sizes:

- **user-application**: `luansantos333/user-application:0.0.3`
- **mail-application**: `luansantos333/mail-application:0.0.2`

### Building Custom Images

If you need to build your own images:

```bash
# Build user-application image
cd user-application
docker build -t your-username/user-application:latest .

# Build mail-application image
cd ../mail-application
docker build -t your-username/mail-application:latest .
```

### Environment Variables

The `docker-compose.yml` file uses environment variables for secure configuration:

#### Security Best Practices
- **Required variables** fail-fast with clear error messages if not provided
- **No hardcoded credentials** in docker-compose.yml
- **Sensitive data** loaded from `.env` file (gitignored)
- **Kafka and Zookeeper** exposed only internally (not accessible from host)
- **PostgreSQL** exposed on port 5432 for manual database initialization

#### User Application Environment Variables
```bash
# Required (must be set in .env file)
CLIENT_ID                         # OAuth2 client ID
CLIENT_SECRET                     # OAuth2 client secret (use strong value)
CLIENT_NAME                       # OAuth2 client name
DB_PASSWORD                       # PostgreSQL password

# Optional (have defaults)
USER_APPLICATION_PROFILE=prod     # Spring profile
USER_APPLICATION_PORT=8081        # Application port
DB_NAME=user-messaging-db         # PostgreSQL database name
DB_USERNAME=java-user             # PostgreSQL username
KAFKA_URI=kafka:9092             # Kafka bootstrap servers
POSTGRES_URL=postgresql           # PostgreSQL host
POSTGRES_PORT=5432               # PostgreSQL port
REDIRECT_URI=https://www.google.com.br # OAuth2 redirect URI
TOKEN_TTL=3600                   # JWT token TTL in seconds
```

#### Mail Application Environment Variables
```bash
# Required (must be set in .env file)
MAIL_HOST                        # SMTP server (e.g., smtp.gmail.com)
MAIL_PORT                        # SMTP port (587 for TLS)
MAIL_USERNAME                    # SMTP username (email address)
MAIL_PASSWORD                    # SMTP password or app password

# Optional (have defaults)
MAIL_APPLICATION_PROFILE=prod    # Spring profile
KAFKA_URL=kafka:9092            # Kafka bootstrap servers
```

#### Network Security
```yaml
# Internal services (not accessible from host)
zookeeper:
  expose: [2181]  # Internal only

kafka:
  expose: [9092]  # Internal only

# External services
postgresql:
  ports: ["5432:5432"]  # Exposed for database initialization

user-app:
  ports: ["8081:8081"]  # REST API and OAuth2 server

mail-app:
  ports: ["8082:8082"]  # Health endpoint
```

### Data Persistence

Docker volumes are used for data persistence:

```yaml
volumes:
  postgres_data:      # PostgreSQL database files
  kafka_data:         # Kafka message logs
  zookeeper_data:     # Zookeeper data
  zookeeper_logs:     # Zookeeper logs
```

Data persists across container restarts unless you use `docker-compose down -v`.

## API Endpoints

### User Management

All user endpoints are prefixed with `/api/user`:

#### Create User
```http
POST /api/user
Content-Type: application/json

{
  "username": "user@example.com",
  "password": "securePassword123"
}
```
- **Access**: Public
- **Response**: Returns `UserSecureDTO` with user ID, username, and roles
- **Side Effect**: Sends welcome email via Kafka

#### Get All Users
```http
GET /api/user
```
- **Access**: ADMIN only (`@PreAuthorize("hasRole('ROLE_ADMIN')")`)
- **Response**: List of all users with secure details

#### Get User by Username
```http
GET /api/user/{username}
```
- **Access**: ADMIN or the user themselves
- **Response**: User details
- **Security**: Validates requester has permission to view this user

#### Delete User
```http
DELETE /api/user/{id}
```
- **Access**: ADMIN or the user themselves
- **Response**: 204 No Content
- **Security**: Validates requester has permission to delete this user

#### Grant Admin Privileges
```http
PATCH /api/user/grant/{username}
```
- **Access**: ADMIN only
- **Response**: 204 No Content
- **Side Effect**: Sends admin access notification email via Kafka

#### Request Password Reset Token
```http
POST /api/user/reset/token
Content-Type: application/json

{
  "username": "user@example.com"
}
```
- **Access**: Public
- **Response**: 204 No Content (token not returned for security)
- **Side Effect**: Sends password reset email with token via Kafka
- **Token Expiry**: 30 minutes
- **Security**: Token is sent only via email, never exposed in API response

#### Reset Password
```http
PUT /api/user/password
Content-Type: application/json

{
  "username": "user@example.com",
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "newPassword": "newSecurePassword123"
}
```
- **Access**: Public
- **Request Body**:
  - `username` (required): User's email address
  - `token` (required): The reset token received via email
  - `newPassword` (required): New password to set
- **Response**: 204 No Content
- **Security**:
  - All sensitive data passed in request body (not query parameters)
  - Validates token exists and hasn't expired
  - Verifies token belongs to the specified user
  - Deletes token after successful password reset
  - Password is BCrypt hashed before storage

### OAuth2 Client Management

```http
# Client endpoints (see ClientController.java)
GET    /api/clients
POST   /api/clients
GET    /api/clients/{id}
PUT    /api/clients/{id}
DELETE /api/clients/{id}
```

### OAuth2 Authorization

The application includes a Spring Authorization Server that supports:
- **OAuth2 Authorization Flows**: Authorization Code, Client Credentials
- **OpenID Connect**: Full OIDC provider capabilities
- **JWT Token Generation**: Customizable token claims via `JwtTokenCustomizer`
- **Client Credentials Management**: Dynamic client registration and management

## Kafka Topics and Events

### Topics

| Topic Name | Producer | Consumer | Event Type |
|------------|----------|----------|------------|
| `user-created` | user-application | mail-application | `UserCreatedEvent` |
| `admin-grant` | user-application | mail-application | `UserAdminAccessGrant` |
| `password-reset` | user-application | mail-application | `PasswordResetTokenEvent` |

### Event DTOs

#### UserCreatedEvent
```java
{
  "userId": 1,
  "timestamp": "2025-11-09T12:00:00Z",
  "email": "user@example.com"
}
```
- **Triggered by**: User creation via POST /api/user
- **Consumer**: `MailService.sendEmailUserCreated()`
- **Email Template**: Welcome message confirming account creation

#### UserAdminAccessGrant
```java
{
  "email": "user@example.com",
  "message": "Your user has been granted administrator permissions.",
  "timestamp": "2025-11-09T12:00:00Z"
}
```
- **Triggered by**: Admin privilege grant via PATCH /api/user/grant/{username}
- **Consumer**: `MailService.sendEmailUserHasAdminAccess()`
- **Email Template**: Admin access notification with custom message

#### PasswordResetTokenEvent
```java
{
  "token": "550e8400-e29b-41d4-a716-446655440000",
  "expirationTime": "2025-11-09T13:00:00Z",
  "username": "user@example.com"
}
```
- **Triggered by**: Password reset request via POST /api/user/reset/token
- **Consumer**: `MailService.sendResetTokenToEmail()` (`MailService.java:61-80`)
- **Email Template**: Password reset token with instructions
  - **Subject**: "Password reset Token"
  - **Body**: Reset token and API endpoint instructions
  - **Token Validity**: 30 minutes
- **Security**: Token is stored in database (`tb_reset_token`) with user association and expiration time

## Security

### Authentication & Authorization

The User Application implements comprehensive security measures:

#### Spring Security
- **Method-level Security**: `@PreAuthorize` annotations on controller methods
- **Role-Based Access Control (RBAC)**: Two roles: `ROLE_USER` and `ROLE_ADMIN`
- **Password Encoding**: BCrypt password hashing via `PasswordEncoder`
- **Context-based Authorization**: Checks in service layer (see `UserService.java:84`, `UserService.java:109`)

#### OAuth2 Authorization Server
- **Embedded Authorization Server**: Full OAuth2/OIDC provider
- **Supported Flows**: Authorization Code, Client Credentials
- **JWT Tokens**: Custom token claims via `JwtTokenCustomizer`
- **Token Configuration**: Configurable TTL via `TOKEN_TTL` environment variable
- **Client Management**: Dynamic OAuth2 client registration

#### Access Control Rules

| Endpoint | Access Rule | Implementation |
|----------|-------------|----------------|
| POST /api/user | Public | No authentication required |
| GET /api/user | ADMIN only | `@PreAuthorize("hasRole('ROLE_ADMIN')")` |
| GET /api/user/{username} | ADMIN or self | Runtime check in `UserService.java:84` |
| DELETE /api/user/{id} | ADMIN or self | Runtime check in `UserService.java:109` |
| PATCH /api/user/grant/{username} | ADMIN only | `@PreAuthorize("hasRole('ROLE_ADMIN')")` |

#### Security Features
- **Secure Password Storage**: BCrypt hashing with salt
- **JWT Token Authentication**: Stateless authentication
- **Authorization Context**: Uses `SecurityContextHolder` for request-scoped security
- **Access Denied Handling**: Throws `AccessDeniedException` for unauthorized access
- **Security Logging**: WARN level in production (application-prod.yml:43), DEBUG in development
- **No Credentials in Code**: All sensitive values loaded from environment variables
- **Password Reset Tokens**: Sent only via email, never in API responses
- **Request Body Security**: Sensitive data (passwords, tokens) in request body, not query params
- **Production SQL Logging**: Disabled (`show-sql: false`) to prevent data leaks

### Security Configuration Files
- `AuthenticationConfig.java`: OAuth2 and JWT configuration
- `JwtTokenCustomizer.java`: Custom JWT claims
- `JwtAuthenticationConverter.java`: JWT to Spring Security authentication conversion
- `CustomOAuth2Provider.java`: Custom OAuth2 provider configuration

### Docker Security
- **Network Isolation**: Kafka and Zookeeper accessible only within Docker network
- **Required Credentials**: docker-compose fails-fast if required environment variables not set
- **No Default Passwords**: All credentials must be explicitly provided via .env file
- **.env File Protection**: .env is gitignored to prevent credential leaks
- **Health Checks**: All services have health checks with proper restart policies
- **Read-only Volumes**: Data volumes for PostgreSQL, Kafka, and Zookeeper persistence

## Database

### Development (H2)
- In-memory database
- Auto-creates schema on startup
- Console accessible at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:mydb`

### Production (PostgreSQL)
Configure in `application-hml.yml` for production deployment.

## Development

### Running Tests

```bash
mvn test
```

### H2 Console Access

When running with the test profile:
1. Navigate to `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:mydb`
3. Username: `sa`
4. Password: `12345`

## Email Notification System

### Mail Application Service

The Mail Application (`mail-application`) is a dedicated microservice responsible for handling all email notifications in the system.

#### Configuration

Email settings in `application.yml`:
```yaml
spring:
  mail:
    host: smtp.example.com          # SMTP server host
    port: 587                        # SMTP port (587 for TLS, 465 for SSL)
    username: your-email@example.com # SMTP username
    password: your-app-password      # SMTP password or app-specific password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
```

#### Kafka Consumer Configuration
```yaml
spring:
  kafka:
    consumer:
      bootstrap-servers: localhost:9092
      group-id: user-created-group
      auto-offset-reset: earliest
```

#### Email Templates

**User Creation Email** (`MailService.java:22-36`):
- **Subject**: "Your new user has been successfully created"
- **Body**: Welcome message confirming account creation
- **Trigger**: Consumes `user-created` topic

**Admin Access Grant Email** (`MailService.java:40-57`):
- **Subject**: "You now have admin access, {email}"
- **Body**: Custom message with admin permissions notification
- **Trigger**: Consumes `admin-grant` topic

**Password Reset Email** (planned):
- **Subject**: "Password Reset Request"
- **Body**: Reset link with temporary token (e.g., `http://app.com/reset?token=abc123`)
- **Trigger**: Will consume `password-reset` topic

### Email Service Features

- **Asynchronous Processing**: Email sending doesn't block user-facing operations
- **Kafka-based Decoupling**: User application doesn't need to know about email infrastructure
- **Centralized Email Logic**: All email templates and sending logic in one place
- **Logging**: INFO level logging for email sending status
- **JavaMailSender Integration**: Spring Boot's email abstraction for easy SMTP configuration

## Messaging Architecture

### Kafka Configuration

#### User Application (Producer)
- **Bootstrap Servers**: Configured via `KAFKA_URI` environment variable
- **Serialization**: JSON serialization for event objects
- **Topics**: Auto-created via `KafkaTopicConfig.java`
- **Service**: `KafkaProducerService.java` handles all event publishing

#### Mail Application (Consumer)
- **Consumer Groups**:
  - `user-created-group` for user creation events
  - `user-admin-grant` for admin grant events
- **Deserialization**: JSON deserialization with type hints
- **Listeners**: `@KafkaListener` annotations on `MailService` methods
- **Error Handling**: Spring Kafka default error handling

### Why Kafka?

This architecture uses Kafka for several key benefits:

1. **Decoupling**: User application doesn't depend on mail service availability
2. **Resilience**: Messages are persisted; mail service can catch up after downtime
3. **Scalability**: Multiple mail service instances can consume from the same topics
4. **Audit Trail**: Kafka retains messages for debugging and compliance
5. **Async Processing**: User creation doesn't wait for email sending
6. **Extensibility**: Easy to add new consumers for the same events (analytics, logging, etc.)

## Microservices Benefits

This project demonstrates key microservices principles:

- **Single Responsibility**: Each service has a clear, focused purpose
- **Independent Deployment**: Services can be deployed separately
- **Technology Flexibility**: Each service can use different tech stacks if needed
- **Fault Isolation**: Mail service failures don't affect user management
- **Scalability**: Scale services independently based on load
- **Event-Driven**: Loose coupling through asynchronous messaging

## API Documentation

### Swagger/OpenAPI

The User Application includes interactive API documentation powered by SpringDoc OpenAPI 3.

#### Access Swagger UI

- **Local Development**: http://localhost:8081/swagger-ui/index.html
- **Docker**: http://localhost:8081/swagger-ui/index.html

The Swagger UI provides:
- **Interactive API Testing**: Try out endpoints directly from the browser
- **Request/Response Examples**: See example payloads for all endpoints
- **Schema Definitions**: View DTO structures and validation rules
- **Authentication**: Test secured endpoints with JWT tokens

#### OpenAPI Specification

- **JSON Format**: http://localhost:8081/v3/api-docs
- **YAML Format**: http://localhost:8081/v3/api-docs.yaml

#### Security Configuration

Swagger endpoints are publicly accessible (configured in `AuthenticationConfig.java:88-90`):
- `/swagger-ui/**` - Swagger UI resources
- `/swagger-ui.html` - Main Swagger page
- `/v3/api-docs/**` - OpenAPI specification

#### Using Swagger with OAuth2

To test secured endpoints:

1. Obtain a JWT token via OAuth2 authorization code flow
2. Click "Authorize" in Swagger UI
3. Enter the token in the format: `Bearer {your-jwt-token}`
4. Test protected endpoints with the authenticated session

## Password Reset Implementation

The password reset feature is **fully implemented** with the following components:

### Database
- **Entity**: `PassswordResetTokenEntity.java` - Stores reset tokens with user association and expiration
- **Table**: `tb_reset_token` - Persists token data
- **Token Validity**: 30 minutes (configurable via `PassswordResetTokenEntity.expirationTimeInMinutes`)

### Endpoints
- **Request Token**: POST /api/user/reset/token (`UserController.java:78-85`)
- **Reset Password**: PUT /api/user/password (`UserController.java:87-96`)

### Service Layer
- **Token Generation**: `UserService.createPasswordResetToken()` (`UserService.java:139-151`)
  - Generates UUID token
  - Stores in database with user association
  - Publishes Kafka event to mail service
- **Password Reset**: `UserService.resetUserPassword()` (`UserService.java:155-174`)
  - Validates token existence and expiration
  - Verifies token ownership
  - Updates password with BCrypt encoding
  - Deletes token after use

### Kafka Integration
- **Event**: `PasswordResetTokenEvent.java` - Contains token, expiration time, and username
- **Topic**: `password-reset`
- **Consumer**: `MailService.sendResetTokenToEmail()` (`MailService.java:61-80`)

### Security Features
- Token must belong to the user requesting password reset
- Tokens expire after 30 minutes
- One-time use (deleted after successful password reset)
- Throws `CredentialExpiredException` for invalid/expired tokens
- Throws `AccessDeniedException` for token ownership violations

## Development Notes

- Email sending in `MailService.java` is **fully implemented** and active
- Kafka producer and consumer are configured with JSON serialization
- The application uses Lombok annotations to reduce boilerplate code
- Spring Security debug logging is enabled by default for development
- H2 console is available for database inspection during development
- Password reset functionality is **fully implemented** and operational

## Troubleshooting

### Kafka Connection Issues
```bash
# Check if Kafka is running
docker ps | grep kafka

# View Kafka logs
docker logs kafka
```

### Email Not Sending
1. Check SMTP credentials in `application.yml`
2. Verify firewall allows SMTP port (587/465)
3. Check mail-application logs for errors
4. Verify Kafka topics are being consumed

### H2 Database Access
1. Navigate to `http://localhost:8080/h2-console`
2. JDBC URL: `jdbc:h2:mem:mydb`
3. Username: `sa`, Password: `12345`

## Testing the Application

### 1. Create a User
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{"username":"test@example.com","password":"password123"}'
```
Expected: User created, email sent to test@example.com

### 2. Grant Admin Access
```bash
# First, authenticate as admin to get token
# Then:
curl -X PATCH http://localhost:8080/api/user/grant/test@example.com \
  -H "Authorization: Bearer {your-token}"
```
Expected: Admin role granted, notification email sent

### 3. List All Users (Admin Only)
```bash
curl -X GET http://localhost:8080/api/user \
  -H "Authorization: Bearer {admin-token}"
```

