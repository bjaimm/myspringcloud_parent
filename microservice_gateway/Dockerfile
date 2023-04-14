FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/microservice_gateway.jar
WORKDIR /work/project
EXPOSE 8888
CMD ["java", "-jar", "microservice_gateway.jar"]