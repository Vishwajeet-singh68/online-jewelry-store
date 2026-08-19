FROM eclipse-temurin:20-jdk-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
COPY src src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:20-jre-alpine
WORKDIR /app
COPY --from=build /app/target/cart-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
