FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/microservice-order.jar
WORKDIR /work/project
EXPOSE 8302
CMD ["java", "-jar", "microservice-order.jar"]