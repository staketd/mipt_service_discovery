FROM ubuntu:20.04

RUN apt-get update && \
    apt-get install -y ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

RUN apt-get update && \
    apt-get install -y openjdk-17-jre && \
    apt-get install -y ant && \
    apt-get clean;

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/servicediscovery/servicediscovery.jar"]

ARG JAR_FILE

ADD target/${JAR_FILE} /usr/share/servicediscovery/servicediscovery.jar