FROM openjdk:21-jdk-slim

WORKDIR /app

COPY gradlew gradlew.bat /app/
COPY gradle /app/gradle
COPY build.gradle settings.gradle /app/
COPY src /app/src

RUN chmod +x ./gradlew

RUN ./gradlew bootJar

EXPOSE 8081

ENV SPRING_REDIS_HOST=redis-service
ENV SPRING_REDIS_PORT=6379
ENV SPRING_PROFILES_ACTIVE=dev

CMD ["java", "-jar", "build/libs/exchange-rate-1.0.0.jar"]