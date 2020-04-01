FROM openjdk:11
VOLUME /tmp
ADD target/aqueducte-0.0.1-SNAPSHOT.jar aqueducte-0.0.1-SNAPSHOT.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","aqueducte-0.0.1-SNAPSHOT.jar"]