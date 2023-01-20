FROM openjdk:8-jdk-alpine as builder
WORKDIR /application
COPY . .
RUN chmod +x ./gradlew \
  && ./gradlew build -x test

FROM openjdk:8-jdk-alpine
ARG DEBIAN_FRONTEND=noninteractive
ENV TZ="Asia/Seoul"
RUN apk add tzdata
WORKDIR /application
ENV VERSION=0.0.1
COPY --from=builder /application/build/libs .
CMD ["sh", "-c", "java -jar /application/spring-minio-template-${VERSION}-SNAPSHOT.jar"]