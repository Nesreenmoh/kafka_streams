FROM openjdk:17
COPY target/kafka-tutorials-1.0-SNAPSHOT-jar-with-dependencies.jar /app/kafka-tutorials-1.0-SNAPSHOT-jar-with-dependencies.jar
ENTRYPOINT ["java", "-jar", "/app/kafka-tutorials-1.0-SNAPSHOT-jar-with-dependencies.jar"]
