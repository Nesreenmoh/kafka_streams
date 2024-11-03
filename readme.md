
### Run docker compose
- Go to the docker compose file and run the following command ```docker-compose -p "kafka-tutorial" up -d```
- If you face any problem with **port is already in used**, run the following command to check with port you need to kill
```sudo lsof -i -P -n | grep 2181```
- You can check the kafka-ui by browse **http://localhost:8080/ui/clusters/local/brokers**


### Create kafka topics
- 
