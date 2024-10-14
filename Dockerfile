FROM maven:3.9.9-amazoncorretto-21-debian

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean install -Dmaven.test.skip=true

COPY target/vetdata-0.0.1-SNAPSHOT.jar /app/vetdata.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/vetdata.jar"]





