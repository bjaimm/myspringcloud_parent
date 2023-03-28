FROM openjdk:8-jdk-alpine
COPY target/microservice-order.jar /work/project/microservice-order.jar
WORKDIR /work/project
CMD ["java", "-jar", "microservice-order.jar"]