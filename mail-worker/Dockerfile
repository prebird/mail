# build
FROM gradle:7.6-jdk-alpine AS builder
WORKDIR /build

# 의존성 받기
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 어플리케이션 빌드
COPY . /build
RUN ./gradlew -x test --no-daemon clean build

# APP
FROM openjdk:17.0-slim
WORKDIR /app

ENV ENV_DIR=src/main/resources
COPY --from=builder /build/build/libs/*.jar .
EXPOSE 8080
ENTRYPOINT java -jar mail-worker.jar
