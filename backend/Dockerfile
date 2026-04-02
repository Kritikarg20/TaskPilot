# Use Java 17
FROM openjdk:17-jdk-slim

# Copy jar file
COPY target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "/app.jar"]