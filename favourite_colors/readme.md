
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

### Create Input kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --topic favourite-colors-input-1```

### create intermediary kafka compacted topic with configuration
- you need to create this inside kafka broker so the port will be different starts from 29092
- ```
  kafka-topics --bootstrap-server kafka1:29092 --create --topic favourite-colors-compact \
  --partitions 1 —replication-factor 1 \
  --config cleanup.policy=compact --config min.cleanable.dirty.ratio=0.005 \ 
  --config segment.ms=10000 ```

### Create Output kafka topics
- Go to the terminal and go inside the one of kafka broker **kafka1** by running this
- ``` docker exec -it kafka1 /bin/bash```
- ```cd /usr/bin```
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --topic favourite-colors-input``` 
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --config cleanup.policy=compact --topic favourite-colors-output``` 
- ```kafka-topics --create --bootstrap-server kafka1:29092 --replication-factor 1 --partitions 2 --config cleanup.policy=compact --topic favourite-colors-intermediate-1``` 

### create a consumer 
- 
```
kafka-console-consumer --bootstrap-server kafka1:29092 --topic favourite-colors-output --from-beginning \
--formatter kafka.tools.DefaultMessageFormatter \
--property print.key=true \
--property print.value=true \
--property key.deserializer=org.apache.kafka.common.serialization.StringDeserializer \
--property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer 
```
### create a producer
- ```kafka-console-producer --bootstrap-server localhost:29092 --topic favourite-colors-input ```

### Some data to be sent by producer
```
anis,green
yusuf,blue
sara,yellow
ali,blue
anis,red
sara,red
yusuf,blue
```
### The compacted results that you should be in the output topic favourite-color-output
```
green,0
red,1
blue,3
```
