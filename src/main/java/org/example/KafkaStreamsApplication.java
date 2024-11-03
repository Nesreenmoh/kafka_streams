package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Arrays;
import java.util.Properties;

public class KafkaStreamsApplication {
  public static void main(String[] args) {


    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "word-count-application");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    // create a streamBuilder
    StreamsBuilder builder = new StreamsBuilder();

    // get the stream from a topic
    KStream<String, String> streamInput = builder.stream("word-count-input");
    // convert the values to lower cases
    KTable<String, Long> wordCount = streamInput.mapValues(mapValues -> mapValues.toLowerCase())
        // create a flatmap by spaces
        .flatMapValues(flatValues -> Arrays.asList(flatValues.split(" ")))

        // select key(null) to be as value
        .selectKey((key, value) -> value)

        // group by key
        .groupByKey()

        // count
        .count();

    // convert the Ktable to Kstream and write it back to another topic
    wordCount.toStream().to("word-count-output", Produced.with(Serdes.String(), Serdes.Long()));

    // Create a kafka stream with the builder and properties

    KafkaStreams streams = new KafkaStreams(builder.build(), properties);

    // start a stream
    streams.start();
  }
}
