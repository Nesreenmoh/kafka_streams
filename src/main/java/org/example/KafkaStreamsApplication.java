package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamsApplication {

  private static final Logger logger = Logger.getLogger(KafkaStreamsApplication.class);

  public static void main(String[] args) {

    logger.info("Application started");


    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-application");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092,localhost:9093,localhost:9094");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());


    // create a streamBuilder


    KafkaStreams streams = new KafkaStreams(new KafkaStreamsApplication().createTopology(), properties);

    // start a stream
    streams.start();

    // print the topology
    System.out.println(streams.toString());

    // add shutdown hook to close gracefully
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
  }

  public Topology createTopology() {
    StreamsBuilder builder = new StreamsBuilder();

    // get the stream from a topic
    KStream<String, String> streamInput = builder.stream("word-count-input");
    // convert the values to lower cases
    KTable<String, Long> wordCount = streamInput.mapValues(mapValues -> mapValues.toLowerCase())
        // create a flatmap by spaces
        .flatMapValues(flatValues -> Arrays.asList(flatValues.split("\\s+")))

        // select key(null) to be as value
        .selectKey((key, value) -> value)

        // group by key
        .groupByKey()

        // count
        .count(Materialized.as("Counts"));

    // convert the Ktable to Kstream and write it back to another topic
    wordCount.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

    // Create a kafka stream with the builder and properties
    return builder.build();
  }
}
