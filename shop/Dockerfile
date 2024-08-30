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
COPY --from=builder /build/build/libs/shop.jar .
EXPOSE 8080
ENTRYPOINT java -jar shop.jar

# docker build --platform linux/amd64 -t gyureal/shop:latest .
# docker push gyureal/shop:latest
# docker run -e "SPRING_PROFILES_ACTIVE=console" shop:latest
