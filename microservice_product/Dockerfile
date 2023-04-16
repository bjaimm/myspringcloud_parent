FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/microservice-product.jar
WORKDIR /work/project
EXPOSE 8201
CMD ["java", "-jar", "microservice-product.jar"]