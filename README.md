# Notification Service
Banking Notification Service for sending email notifications for high-value transactions and account status changes.

## Author & Repository
### Name
Samarth Kulkarni

### BITS ID
2024tm93040

### GitHub repository
https://github.com/samarthsk/notification-service

## Features
- Email notifications for transactions ≥ ₹50,000
- Account status change notifications
- RabbitMQ asynchronous messaging
- REST API endpoints
- PostgreSQL database
- Swagger API documentation
- Prometheus metrics

## Tech Stack
- Java 21
- Spring Boot 3.3.5
- PostgreSQL
- RabbitMQ
- Maven

## Prerequisites
- Java 21+
- Maven 3.9+
- PostgreSQL 14+
- RabbitMQ 3.12+

## Setup Instructions
### 1. Database Setup
CREATE DATABASE notification_db;

### 2. Configure Application
Edit `src/main/resources/application.properties`:
- Set PostgreSQL password
- Set Gmail credentials
- Configure RabbitMQ (default: localhost:5672)

### 3. Build and Run
mvn clean install
mvn spring-boot:run

### 4. Access Points
- Application: http://localhost:8083
- Swagger UI: http://localhost:8083/swagger-ui.html
- Health Check: http://localhost:8083/actuator/health
- Metrics: http://localhost:8083/actuator/prometheus

## API Endpoints
### Notifications
- POST `/api/notifications/transaction` - Send transaction notification
- POST `/api/notifications/account-status` - Send account status notification
- GET `/api/notifications` - Get all notifications
- GET `/api/notifications/recent` - Get recent notifications
- GET `/api/notifications/{id}` - Get notification by ID
- POST `/api/notifications/retry-failed` - Retry failed notifications

## RabbitMQ Queues
### Listening On:
- `transaction.notification.queue` - Transaction notifications
- `account.status.queue` - Account status changes

## Docker Instructions
### Build the Project JAR
mvn clean package -DskipTests

### Build Docker Image
docker build -t notification-service:latest .

### List Your Image
docker images | grep notification-service

### Launch Supporting Services with Docker Compose
If you have a docker-compose.yml with PostgreSQL and RabbitMQ containers configured, 
you do not need to start them locally: docker-compose up -d

This starts all services in the background. 
Check containers with: docker ps

### Run Notification Service Container
Substitute your real email credentials as needed.

docker run --rm -p 8083:8083 \
-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/notification \
-e SPRING_DATASOURCE_USERNAME=postgres \
-e SPRING_DATASOURCE_PASSWORD=sam \
-e SPRING_RABBITMQ_HOST=host.docker.internal \
-e SPRING_MAIL_USERNAME=YOUR_EMAIL_HERE \
-e SPRING_MAIL_PASSWORD=YOUR_APP_PASSWORD \
notification-service:latest

_host.docker.internal_ allows your container to reach services running on your host machine (works for Docker Desktop Mac/Windows). 
If running all with compose, use the relevant service names as host values instead.

## Environment Variables
- SPRING_DATASOURCE_URL : Database URL 
- SPRING_DATASOURCE_USERNAME : Database username 
- SPRING_DATASOURCE_PASSWORD : Database password 
- SPRING_RABBITMQ_HOST : RabbitMQ host 
- SPRING_MAIL_USERNAME : Email username 
- SPRING_MAIL_PASSWORD : Email password

## Test the API
Open http://localhost:8083/swagger-ui.html in your browser, or use Postman to send requests to your endpoints.

