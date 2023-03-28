FROM openjdk:8-jdk-alpine
COPY target/microservice_product-1.0-SNAPSHOT.jar /work/project/microservice-product.jar
WORKDIR /work/project
CMD ["java", "-jar", "microservice-product.jar"]