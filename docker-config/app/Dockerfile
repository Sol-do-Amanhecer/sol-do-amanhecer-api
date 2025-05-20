FROM maven:3.8.7-eclipse-temurin-19

WORKDIR /app

COPY . .

RUN mvn clean install

CMD ["java", "-jar", "target/sol-do-amanhecer-api-0.0.1-SNAPSHOT.jar"]

EXPOSE ${APP_PORT}
