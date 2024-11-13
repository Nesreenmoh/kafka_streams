package org.example;



import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BankAccountBalanceStreamApplication {



  public static void main(String[] args) {


    System.out.println("Bank Account Balance Stream");


    KafkaStreams kafkaStreams = new KafkaStreams(new BankAccountBalanceStreamApplication().createTopology(), config());
    kafkaStreams.start();
    System.out.println(kafkaStreams);

    // Shutdown hook
    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }


  private static Properties config() {
    Properties props = new Properties();

    props.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-account-balance-stream");
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, io.confluent.kafka.streams.serdes.avro.GenericAvroSerde.class.getName());
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//    props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
    props.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");
    props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_DOC, "0");
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, "12000");
    props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return props;
  }


  private Topology createTopology() {

    StreamsBuilder builder = new StreamsBuilder();
    // Create a map for the SerDe configuration
    Map<String, String> serdeConfig = new HashMap<>();
    serdeConfig.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

    // Configure the GenericAvroSerde
    GenericAvroSerde avroSerde = new GenericAvroSerde();
    avroSerde.configure(serdeConfig, false); // `false` for value SerDe
    KStream<String, GenericRecord> output = builder.stream("bank-balance-events-topic-2",
        Consumed.with(Serdes.String(), avroSerde) );

    output
        .groupByKey()
        .aggregate(
            () -> 0.0,
            (key,value,agg) -> agg + (Double) value.get("Amount"),
            Materialized.<String, Double, KeyValueStore<Bytes, byte[]>>as("bank-balance-agg")
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Double())
        )
        .toStream()
        .to("aggregated-bank-account-balance-topic-1");

    return builder.build();
  }
}
