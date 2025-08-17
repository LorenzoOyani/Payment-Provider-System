# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy Maven/Gradle wrapper and dependency files
COPY mvnw* ./
COPY .mvn .mvn
COPY pom.xml ./

# Copy source code
COPY src ./src

# Make Maven wrapper executable
RUN chmod +x ./mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a new stage for runtime
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=0 /app/target/*.jar app.jar

# Expose the port your Spring Boot app runs on
EXPOSE 8081

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]