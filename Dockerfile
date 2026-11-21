FROM maven:3.9-eclipse-temurin-25 AS build
WORKDIR /workspace

COPY pom.xml .
COPY src/main ./src/main
RUN mvn -B -DskipTests package

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build --chown=app:app /workspace/target/vollzanbel-*.jar /app/app.jar

USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
