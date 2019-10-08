FROM maven:3.6-jdk-11

ENV DEBIAN_FRONTEND=noninteractive

COPY src /jsh/src
COPY pom.xml /jsh/pom.xml
COPY jsh /jsh/jsh
COPY analysis /jsh/analysis
COPY test /jsh/test
COPY coverage /jsh/coverage

RUN cd /jsh && mvn package

ENV DEBIAN_FRONTEND=

EXPOSE 8000

