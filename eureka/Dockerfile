FROM openjdk:8-jdk-alpine
ARG JAR_FILE
COPY ${JAR_FILE} /work/project/eureka.jar
WORKDIR /work/project
EXPOSE 6868
CMD java -jar eureka.jar