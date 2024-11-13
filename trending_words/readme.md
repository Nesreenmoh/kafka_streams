
### Run docker compose
- Go to the docker compose file and run the following command ```docker-compose -p "kafka-tutorial" up -d```
- If you face any problem with **port is already in used**, run the following command to check with port you need to kill
```sudo lsof -i -P -n | grep 2181```
- You can check the kafka-ui by browse **http://localhost:8080/ui/clusters/local/brokers**
-  check the network of the container ```docker inspect network kafka2```
- To build your application and make it as container ```docker build -t kafka-tutorials-app .```
- Run your image inside the same network of kafka cluster ```docker run --network <network-name> -d <image-name>```


### List kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this 
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --list --bootstrap-server kafka1:9092``` to list the topics inside

### Create kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --topic top-n-trending-words-input-topic-2``` 
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --topic trending-words-output-topic-3``` 

### create kafka compacted topic with configuration
- you need to create this inside kafka broker so the port will be different starts from 29092
- ```
  kafka-topics --bootstrap-server kafka1:29092 --create --topic trending-words-output-topic-3 \
  --partitions 1 —replication-factor 1 \
  --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.005 --config segment.ms=10000 ```
  
### create a consumer 
- 
```
kafka-console-consumer --bootstrap-server kafka1:29092 --topic trending-words-output-topic-3 --from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer 
```
### create a producer
- ```kafka-console-producer --bootstrap-server localhost:29092 --topic top-n-trending-words-input-topic-2 ```

### Some data to be send by producer
```
123,{"join":"1000"}
456,{"Mark":"2000"}
789,{"Lisa":"30000"}
344,{"Nesreen":"40000"}
789,{"Lisa":"80000"}
456,{"Mark":"90000"}
444,{"Nana:"45555"}
```
