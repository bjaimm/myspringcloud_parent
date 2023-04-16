FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/microservice_security.jar
WORKDIR /work/project
EXPOSE 8401
CMD ["java", "-jar", "microservice_security.jar"]