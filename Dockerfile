FROM maven:3.9.9-amazoncorretto-21-debian AS builder

WORKDIR /app

COPY . ./

RUN mvn dependency:go-offline -B

RUN mvn clean package

# Fase de execução
FROM amazoncorretto:21

WORKDIR /app

COPY --from=builder /app/target/*.jar /app/vetdata.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/vetdata.jar"]





