FROM openjdk:8
COPY target/microservice-config-1.0-SNAPSHOT.jar /work/project/configserver.jar
ADD https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar /opt/aws-opentelemetry-agent.jar
ENV JAVA_TOOL_OPTIONS=-javaagent:/opt/aws-opentelemetry-agent.jar
WORKDIR /work/project
CMD java -jar configserver.jar