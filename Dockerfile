FROM eclipse-temurin:21-jre-alpine
LABEL authors="lsh80165@gmail.com"

# ADB만 설치 (최소한의 패키지)
RUN apk add --no-cache wget unzip bash \
    && cd /tmp \
    && wget https://dl.google.com/android/repository/platform-tools-latest-linux.zip \
    && unzip platform-tools-latest-linux.zip \
    && mv platform-tools/adb /usr/local/bin/ \
    && chmod +x /usr/local/bin/adb \
    && rm -rf /tmp/platform-tools* \
    && rm platform-tools-latest-linux.zip

WORKDIR /app

EXPOSE 8080

COPY build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
