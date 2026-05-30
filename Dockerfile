# STAGE 1: Build the application
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Yeh command test cases skip karke JAR file banayegi
RUN mvn clean package -DskipTests

# STAGE 2: Run the application
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Pichle stage se banayi hui JAR file ko naye container mein copy karo
COPY --from=build /app/target/*.jar app.jar
# Render ko batao ki backend 8080 par chalega
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]