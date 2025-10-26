# ==========================
# Gradle 빌드
FROM gradle:8.8-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 최적화
COPY build.gradle settings.gradle ./
COPY gradle gradle
RUN gradle dependencies --no-daemon || return 0

# 전체 소스 복사
COPY . .

# Firebase & Apple Key 자동 생성
ARG FIREBASE_KEY_JSON
ARG APPLE_PRIVATE_KEY

RUN mkdir -p src/main/resources/firebase \
    && echo "$FIREBASE_KEY_JSON" | tr -d '\n' | base64 -d > src/main/resources/firebase/serviceAccountKey.json \
    && mkdir -p src/main/resources/keys \
    && echo "$APPLE_PRIVATE_KEY" | base64 -d > src/main/resources/keys/apple-private-key.p8

# 빌드 실행
RUN gradle clean bootJar -x test --no-daemon


# ==========================
# 런타임
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Spring Boot 환경변수
ENV SPRING_PROFILES_ACTIVE=prod

# HTTPS 포트 개방
EXPOSE 443

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
