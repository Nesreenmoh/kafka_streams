package org.example;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Logger;
import org.example.model.BankAccountBalanceEvent;

import java.util.Arrays;
import java.util.Properties;

public class BankBalanceConsumerApplication {

  private static final Logger LOG = Logger.getLogger(BankBalanceConsumerApplication.class);



  public static void main(String[] args) {

    String TOPIC = "bank-balance-events-topic-2";

    System.out.println("Bank Account Balance Consumer Application");


    Properties props = new Properties();

    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
        KafkaAvroDeserializer.class.getName());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "bank_balance_consumer");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
    props.put("schema.registry.url", "http://localhost:8081");


    KafkaConsumer<String, BankAccountBalanceEvent> consumer = new KafkaConsumer<>(props);

    consumer.subscribe(Arrays.asList(TOPIC));

    while (true) {
      ConsumerRecords<String, BankAccountBalanceEvent> consumerRecords = consumer.poll(100);
      for (ConsumerRecord<String, BankAccountBalanceEvent> record : consumerRecords) {
        LOG.info("Key " + record.key() + ", and the Value " + record.value());
      }
      // since commitAsync() does not retry on failure, I added a callback to handle any commit exceptions
      consumer.commitAsync((offsets, exception) -> {
        if (exception != null) {
          LOG.error("Error while committing offsets", exception);
        }
      });
    }

  }
}
