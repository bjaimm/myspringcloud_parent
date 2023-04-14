FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/configserver.jar
WORKDIR /work/project
EXPOSE 8001
CMD java -jar configserver.jar