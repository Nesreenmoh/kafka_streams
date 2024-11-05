
### Run docker compose
- Go to the docker compose file and run the following command ```docker-compose -p "kafka-tutorial" up -d```
- If you face any problem with **port is already in used**, run the following command to check with port you need to kill
```sudo lsof -i -P -n | grep 2181```
- You can check the kafka-ui by browse **http://localhost:8080/ui/clusters/local/brokers**
-  check the network of the container ```docker inspect network kafka2```


### List kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this 
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --list --bootstrap-server kafka1:9092``` to list the topics inside

### Create kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --topic word-count-output``` 

### create a consumer 
- ```kafka-console-consumer --bootstrap-server kafka1:29092 --topic word-count-output --from-beginning  --formatter kafka.tools.DefaultMessageFormatter   --property print.key=true   --property print.value=true   --property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer --property value.deserializer=org.apache.kafka.common.serialization.StringDeserializer```

### create a producer
- ```kafka-console-producer --bootstrap-server localhost:29092 --topic word-count-input```
