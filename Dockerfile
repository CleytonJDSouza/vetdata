FROM openjdk:21-jdk-slim

WORKDIR /app

COPY target/vetdata-0.0.1-SNAPSHOT.jar /app/vetdata.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/vetdata.jar"]