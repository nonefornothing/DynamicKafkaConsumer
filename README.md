# Documentation

This repo use for managing consumer kafka that consumer send data to specific destination using rest API.  
This application support to :
    
  - create consumer 
  - check active consumers
  - activate consumer
  - deactivate consumer
  - pause consumer
  - resume consumer

Technology used for this repo :
- Java 8
- Spring Kafka
- Spring Web
- Broker Kafka

## Features

- Consume data from broker kafka
- Send consumed data from kafka to specific destination using rest API . If data failed to sent , then resending data scenario will play role , explain below :
  https://github.com/nonefornothing//blob/java8-containerListener/Resending%20scenario.png
![producer-scenario](https://github.com/nonefornothing/DynamicKafkaConsumer/blob/java8-containerListener/ProducerDesignForPE-diagrameditor.jpg)

## How to use

### Local
- Make sure your broker kafka is alive
- change destination broker kafka to your local or remote broker, example :
   ```sh
   kafka.consumer.broker=localhost:8080
   ```

### Deploy to web application server [war]

- in maven terminal, bundle project to war file
   ```sh
   mvn clean package
   ```
- in project directory, go to target folder and move war file to your web application service

## Reference

### Producer Config
- https://newrelic.com/blog/best-practices/kafka-best-practices
- https://granulate.io/optimizing-kafka-performance/
- https://strimzi.io/blog/2020/10/15/producer-tuning/
- https://towardsdatascience.com/10-configs-to-make-your-kafka-producer-more-resilient-ec6903c63e3f
- https://www.confluent.io/blog/error-handling-patterns-in-kafka/
- Create topic automatically  if return null topic exception ==> solved by set auto.create.topic configuration in broker level to "true"

### ERROR HANDLING WHILE PRODUCING DATA
- https://serkansakinmaz.medium.com/error-handling-in-kafka-producer-edfc05bcbbbf
- https://stackoverflow.com/questions/61654578/kafka-producer-callback-exception

   ```sh
   Note :
    Exception that can be throw while produce data already handling in retries config , so no need to handle it manually  if non retries thrown , then it's not caused poison pill for application
   ```


### Producer not need to flush after produce data
   ```sh
   Note :
    Producer not need to flush after produce data because its can block next message for send to broker  can be used for prevent data loss , if you call it , before you shutdown the producer  if you just shutdown the service by force, than data loss can be happen  if you call flush programmatically , then data loss can not be happen you implement flush , you are effectively implementing a sync producer
(which you shouldn't, see here: https://github.com/edenhill/librdkafka/wiki/FAQ#why-is-there-no-sync-produce-interface).
   ```

### Testing block with no message loss
- https://jack-vanlightly.com/blog/2018/9/14/how-to-lose-messages-on-a-kafka-cluster-part1
- https://jack-vanlightly.com/blog/2018/9/18/how-to-lose-messages-on-a-kafka-cluster-part-2

## License

MIT

**Free Software, Hell Yeah!**




Note : 
Need cron job for cleaning data automatically for data log

You can download postman collection below :
https://www.getpostman.com/collections/1136752094c9f7d529e6


