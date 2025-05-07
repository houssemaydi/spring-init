FROM eclipse-temurin:21-jdk as build
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Fix permissions on mvnw
RUN chmod +x ./mvnw

# Download dependencies first (better caching)
RUN ./mvnw dependency:go-offline -B

COPY src src

# Set file.encoding explicitly for the build
RUN ./mvnw package -DskipTests -Dfile.encoding=UTF-8

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built jar file
COPY --from=build /app/target/*.jar app.jar

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
