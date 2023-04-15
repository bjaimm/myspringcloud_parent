FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/microservice-user.jar
EXPOSE 8081
WORKDIR /work/project
CMD ["java", "-jar", "microservice-user.jar"]