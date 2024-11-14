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

import java.util.Arrays;
import java.util.Properties;

public class TopNTrendingWordsApplication {
  public static void main(String[] args) {


    KafkaStreams streams =
        new KafkaStreams(new TopNTrendingWordsApplication().createTopology(),config());

    streams.start();


    // print stream
    System.out.println(streams);

    // add shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

  }

  public  Topology createTopology() {

    StreamsBuilder builder = new StreamsBuilder();
    KStream<String, String> textInput = builder.stream("top-n-trending-words-input-topic-2");



    KTable<String, Long> countColors =
        textInput.flatMapValues((line) -> Arrays.asList(line.split("\\s+")))
            .selectKey((key, value) -> value)
            .groupByKey()
            .count();

    countColors
        .toStream()
        .groupByKey()
        .aggregate(() -> 0L,
                (key,value,maxValue) -> Math.max(maxValue,value),
                Materialized.with(Serdes.String(),Serdes.Long()))
            .toStream()
            .to("trending-words-output-topic-4", Produced.with(Serdes.String(), Serdes.Long()));
    return builder.build();
  }


  public static Properties config(){

    Properties prop = new Properties();

    prop.put(StreamsConfig.APPLICATION_ID_CONFIG, "Top-N-Trending-Words");
    prop.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    prop.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    prop.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    prop.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    return prop;
  }
}
