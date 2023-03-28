FROM openjdk:8-jdk-alpine
COPY target/microservice_gateway-1.0-SNAPSHOT.jar /work/project/microservice_gateway.jar
WORKDIR /work/project
CMD ["java", "-jar", "microservice_gateway.jar"]