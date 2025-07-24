FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY target/transaction-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
