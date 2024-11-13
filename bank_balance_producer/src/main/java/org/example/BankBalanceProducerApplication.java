package org.example;

import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.example.model.BankAccountBalanceEvent;

import java.time.ZonedDateTime;
import java.util.Properties;
import java.util.Random;

import static java.lang.Thread.sleep;

public class BankBalanceProducerApplication {
  public static void main(String[] args) throws InterruptedException {

    Properties properties = new Properties();
    final String TOPIC = "bank-balance-events-topic-2";
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put(ProducerConfig.RETRIES_CONFIG, "3");
//    properties.put(ProducerConfig.ACKS_CONFIG, "all"); // producing guarantee
    properties.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true"); // idempotent producer
    properties.put(ProducerConfig.LINGER_MS_CONFIG, "1");

    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put("schema.registry.url", "http://localhost:8081");

    KafkaProducer<String, BankAccountBalanceEvent> kafkaProducer =
        new KafkaProducer<String,BankAccountBalanceEvent>(properties);



    for(int i=0; i<5; i++){
      String name = selectRandomName();
      BankAccountBalanceEvent event= BankAccountBalanceEvent.newBuilder()
          .setName(name)
          .setAmount(new Random().nextInt(1000))
          .setTime(ZonedDateTime.now().toInstant().toEpochMilli())
          .build();
      ProducerRecord record = new ProducerRecord(TOPIC,event.getName(),event);
      kafkaProducer.send(record);
      System.out.println("Sent event: " + event.getName());
      sleep(1000);
    }
  }

  private static String selectRandomName() {
    String[] names = {"Nesreen", "Anis", "Sara", "Yusuf", "Nelson"};
    Random random = new Random();
    return names[random.nextInt(names.length)];
  }
}
