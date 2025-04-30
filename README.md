# Demo4 Application

A Spring Boot application with advanced security features, including JWT authentication, role-based authorization, AOP, and monitoring.

## Features

- JWT-based authentication and authorization
- Role-based access control
- Aspect-oriented programming for logging, performance monitoring, and security auditing
- Spring Boot Actuator for monitoring
- Flyway database migrations
- OpenAPI/Swagger documentation

## Docker Support

The application has been containerized with Docker for easy deployment.

### Running with Docker

1. Build and run the application using Docker Compose:

```bash
docker-compose up -d
```

2. To stop the container:

```bash
docker-compose down
```

### Building the Docker image manually

```bash
docker build -t demo4:latest .
```

### Running the Docker container manually

```bash
docker run -p 8080:8080 demo4:latest
```

## Development

### Prerequisites

- Java 21
- Maven

### Running locally

```bash
./mvnw spring-boot:run
```

### Building the application

```bash
./mvnw clean package
```

## API Documentation

Once the application is running, you can access the API documentation at:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI definition: http://localhost:8080/v3/api-docs

## Monitoring

Spring Boot Actuator endpoints are available at:

- http://localhost:8080/actuator/health
- http://localhost:8080/actuator/info
- http://localhost:8080/actuator/metrics