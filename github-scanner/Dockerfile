FROM maven:3.9.8-eclipse-temurin-21 AS builder

WORKDIR /workspace

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /application

COPY --from=builder /workspace/target/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
