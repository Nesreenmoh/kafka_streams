package org.example;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.log4j.Logger;

import java.util.Properties;


public class FavouriteColorsApplication {

  private static final Logger logger = Logger.getLogger(FavouriteColorsApplication.class);

  public static void main(String[] args) {

    logger.info("Favourite Colors Application started");

    // create kafkaStreams
    KafkaStreams streams = new KafkaStreams(new FavouriteColorsApplication().createToplogy(), config());

    // start the stream
    streams.cleanUp();
    streams.start();

    System.out.println(streams);

    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

  }

  // Topology
  private Topology createToplogy() {

    // create a stream
    StreamsBuilder builder = new StreamsBuilder();

    // Read from the input topic
    KStream<String, String> input = builder.stream("favourite-colors-input");


    // transformation process

    KStream<String, String> cleanFavouriteColors = input.filter((key, text) -> text.contains(","))
        .selectKey((key, value) -> value.split(",")[0].toLowerCase())
        .mapValues(value -> value.split(",")[1].toLowerCase())
        .filter((key, value) -> StringUtils.containsAny(value, "blue", "red", "green"));


    // write it to a topic
    cleanFavouriteColors.to("favourite-colors-intermediate-1");

    // read from a topic as ktable -> so the new values will be as update
    KTable<String, String> table = builder.table("favourite-colors-intermediate-1");

    // group it by key and count this opertaion will return ktable
    KTable<String, Long> countColor =
        table.groupBy((key, value) -> new KeyValue<>(value, value)).count();

    // convert ktable to a stream and write it to output topic
    countColor.toStream()
        .to("favourite-colors-output", Produced.with(Serdes.String(), Serdes.Long()));



    return builder.build();
  }


  // Streams configuration
  private static Properties config() {

    Properties props = new Properties();
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "favourite-colors-app");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_DOC,"0");
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG,
        "localhost:9092,localhost:9093,localhost:9094");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    return props;
  }
}
