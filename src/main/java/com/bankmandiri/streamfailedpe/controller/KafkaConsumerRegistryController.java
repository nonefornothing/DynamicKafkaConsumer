package com.bankmandiri.streamfailedpe.controller;

import com.bankmandiri.streamfailedpe.container.CustomKafkaContainerRegistration;
import com.bankmandiri.streamfailedpe.model.KafkaConsumerAssignmentResponse;
import com.bankmandiri.streamfailedpe.model.KafkaConsumerResponse;
import com.bankmandiri.streamfailedpe.model.Request;
import com.bankmandiri.streamfailedpe.utils.CustomHeader;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/consumer-management")
public class KafkaConsumerRegistryController {

    private final Logger logger = LoggerFactory.getLogger(KafkaConsumerRegistryController.class);

    @Autowired
    private CustomKafkaContainerRegistration customKafkaContainerRegistration;

    @GetMapping
    public ResponseEntity<List<KafkaConsumerResponse>> getConsumerIds() {
        List<KafkaConsumerResponse> consumers;
        consumers = customKafkaContainerRegistration.getAllIds()
                .stream()
                .map(this::createKafkaConsumerResponse)
                .collect(Collectors.toList());
        logger.info("List of consumer created : " + consumers);
        return new ResponseEntity<>(consumers,CustomHeader.setHeaders(),HttpStatus.OK);
    }

    @PostMapping(path = "/create")
    public ResponseEntity<String> createConsumer(@RequestBody Request request) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(request.getConsumerId());
        if (Objects.isNull(listenerContainer)) {
            try{
                customKafkaContainerRegistration.registerCustomKafkaContainer(request);
                logger.info("Consumer with id " + request.getConsumerId() + " created ");
                return new ResponseEntity<>("Consumer with id " + request.getConsumerId() + " created ",CustomHeader.setHeaders(),HttpStatus.CREATED);
            }catch (Exception e){
                Objects.requireNonNull(logger).error("Consumer with id " + request.getConsumerId() + " not created yet , ERROR || " + e.getMessage());
                return new ResponseEntity<>("Consumer with id " + request.getConsumerId() + " not created yet , ERROR || " + e.getMessage(),CustomHeader.setHeaders(),HttpStatus.OK);
            }
        } else {
            logger.error("consumer with id " + request.getConsumerId() +" already created ");
            return new ResponseEntity<>("Consumer with id " + request.getConsumerId() + " is already created ",CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/activate")
    public ResponseEntity<String> activateConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            logger.error("Consumer with id " + consumerId + " not found");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not found",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (listenerContainer.isRunning()) {
            logger.error("Consumer with id " + consumerId + "  already running");
            return new ResponseEntity<>("Consumer with id " + consumerId + " already running ",CustomHeader.setHeaders(),HttpStatus.OK);
        } else {
            listenerContainer.start();
            logger.info("Running a consumer with id " + consumerId);
            return new ResponseEntity<>("Running a consumer with id " + consumerId,CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/deactivate")
    public ResponseEntity<String> deactivateConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            logger.error("Consumer with id " + consumerId + " not found");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not found",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (!listenerContainer.isRunning()) {
            logger.error("Consumer with id " + consumerId + "  already stop");
            return new ResponseEntity<>("Consumer with id " + consumerId + " already stop ",CustomHeader.setHeaders(),HttpStatus.OK);
        } else {
            listenerContainer.stop();
            logger.info("Stopping a consumer with id " + consumerId);
            return new ResponseEntity<>("Stopping a consumer with id " + consumerId,CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/pause")
    public ResponseEntity<String> pauseConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            logger.error("Consumer with id " + consumerId + " not found");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not found",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (!listenerContainer.isRunning()) {
            logger.error("Consumer with id " + consumerId + " not running");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not running",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (listenerContainer.isContainerPaused()) {
            logger.error("Consumer with id " + consumerId + "  already stop");
            return new ResponseEntity<>("Consumer with id " + consumerId + " already stop ",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (listenerContainer.isPauseRequested()) {
            logger.error("Consumer with id " + consumerId + "  already requested to be paused");
            return new ResponseEntity<>("Consumer with id " + consumerId + " already requested to be paused",CustomHeader.setHeaders(),HttpStatus.OK);
        } else {
            listenerContainer.pause();
            logger.error("Pause consumer with id " + consumerId);
            return new ResponseEntity<>("Consumer with id " + consumerId + " paused",CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/resume")
    public ResponseEntity<String> resumeConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            logger.error("Consumer with id " + consumerId + " not found");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not found",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (!listenerContainer.isRunning()) {
            logger.error("Consumer with id " + consumerId + " not running");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not running",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (!listenerContainer.isContainerPaused()) {
            logger.error("Consumer with id " + consumerId + " not paused");
            return new ResponseEntity<>("Consumer with id " + consumerId + "not paused",CustomHeader.setHeaders(),HttpStatus.OK);
        } else {
            listenerContainer.resume();
            logger.error("Resuming consumer with id " + consumerId);
            return new ResponseEntity<>("Consumer with id " + consumerId + " resumed",CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }

    @PostMapping(path = "/remove")
    public ResponseEntity<String> removeConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            logger.error("Consumer with id " + consumerId + " not found");
            return new ResponseEntity<>("Consumer with id " + consumerId + " not found",CustomHeader.setHeaders(),HttpStatus.OK);
        } else if (listenerContainer.isRunning() || listenerContainer.isContainerPaused() || listenerContainer.isPauseRequested()) {
            listenerContainer.stop();
            customKafkaContainerRegistration.removeContainer(consumerId);
            logger.error("Consumer with id " + consumerId + " removed");
        } else if (!listenerContainer.isRunning()){
            customKafkaContainerRegistration.removeContainer(consumerId);
            logger.error("Consumer with id " + consumerId + " removed");
        }
        return new ResponseEntity<>("Consumer with id " + consumerId + " removed",CustomHeader.setHeaders(),HttpStatus.OK);
    }

    @GetMapping(path = "/remove-all-consumer")
    public ResponseEntity<String> removeAllConsumer() {
        MessageListenerContainer listenerContainer;
        try {
            for (String s : customKafkaContainerRegistration.getAllIds()) {
                listenerContainer = customKafkaContainerRegistration.getContainer(s);
                if (listenerContainer.isRunning()){
                    listenerContainer.stop();
                    logger.info("Consumer with id " + s + " was active and stopped now ");
                }
            }
            customKafkaContainerRegistration.removeAllContainer();
            logger.info("All consumer deleted");
            return new ResponseEntity<>("All consumer removed",CustomHeader.setHeaders(),HttpStatus.OK);
        }catch (Exception e){
            logger.error("Error while remove all consumer | " + e.getMessage());
            return new ResponseEntity<>("Error while remove all consumer | " + e.getMessage(),CustomHeader.setHeaders(),HttpStatus.OK);
        }
    }


    private KafkaConsumerResponse createKafkaConsumerResponse(String consumerId) {
        MessageListenerContainer listenerContainer =
                customKafkaContainerRegistration.getContainer(consumerId);
        String status = null;
        if (listenerContainer.isRunning()){
            if(listenerContainer.isContainerPaused()){
                status = "pause";
            }else if (listenerContainer.isPauseRequested()){
                status = "pause requested";
            }else{
                status = "active";
            }
        }else if(!listenerContainer.isRunning()){
            status = "inactive";
        }

        return KafkaConsumerResponse.builder()
                .consumerId(consumerId)
                .groupId(listenerContainer.getGroupId())
                .status(status)
                .assignments(Optional.ofNullable(listenerContainer.getAssignedPartitions())
                        .map(topicPartitions -> topicPartitions.stream()
                                .map(this::createKafkaConsumerAssignmentResponse)
                                .collect(Collectors.toList()))
                        .orElse(null))
                .build();
    }

    private KafkaConsumerAssignmentResponse createKafkaConsumerAssignmentResponse(
            TopicPartition topicPartition) {
        return KafkaConsumerAssignmentResponse.builder()
                .topic(topicPartition.topic())
                .partition(topicPartition.partition())
                .build();
    }
}
