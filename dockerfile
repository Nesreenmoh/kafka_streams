FROM openjdk:17
COPY target/kafka-tutorials-1.0-SNAPSHOT.jar /app/kafka-tutorials-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "jar", "/app/kafka-tutorials-1.0-SNAPSHOT.jar"]
