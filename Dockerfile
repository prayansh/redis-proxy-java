#FROM java:8
#VOLUME /tmp
#ADD target/demo-0.0.1-SNAPSHOT.jar demo-0.0.1-SNAPSHOT.jar
#RUN bash -c 'touch /demo-0.0.1-SNAPSHOT.jar'
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/demo-0.0.1-SNAPSHOT.jar"]
#
FROM maven:3.5-jdk-8
VOLUME /tmp
COPY ./ ./
RUN mvn package -Dmaven.test.skip=true
EXPOSE 8080
ENTRYPOINT ["java","-jar","-Dspring.config.location=./docker.properties","target/demo-0.0.1-SNAPSHOT.jar"]
