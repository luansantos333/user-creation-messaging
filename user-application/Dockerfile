  FROM maven:3.9-eclipse-temurin-21 AS build

  WORKDIR /app

  COPY pom.xml .
  RUN mvn dependency:go-offline -B

  COPY src ./src

  RUN mvn clean package -DskipTests

  FROM eclipse-temurin:21-jre-alpine

  WORKDIR /app

  COPY --from=build /app/target/user-application-0.0.1-SNAPSHOT.jar app.jar

  EXPOSE 8081

  ENV USER_APPLICATION_PROFILE=${USER_APPLICATION_PROFILE:-prod}

  ENTRYPOINT ["java", "-jar", "app.jar"]


