# Use a base image with OpenJDK 17
FROM openjdk:21-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY build/libs/student-crm-service-0.0.1-SNAPSHOT.jar /app

# Expose port 8080 for the application
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "student-crm-service-0.0.1-SNAPSHOT.jar"]
