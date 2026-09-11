FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -ntp dependency:go-offline
COPY src ./src
RUN mvn -B -ntp clean package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S escuela && adduser -S escuela -G escuela
COPY --from=build /workspace/target/sistema-administrativo-escolar-*.jar app.jar
USER escuela
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
