FROM eclipse-temurin:21-jre-alpine
LABEL authors="lsh80165@gmail.com"

WORKDIR /app

EXPOSE 8080

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
