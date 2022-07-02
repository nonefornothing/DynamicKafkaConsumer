package com.bankmandiri.streamfailedpe.controller;

import com.bankmandiri.streamfailedpe.container.CustomKafkaContainerRegistration;
import com.bankmandiri.streamfailedpe.model.KafkaConsumerAssignmentResponse;
import com.bankmandiri.streamfailedpe.model.KafkaConsumerResponse;
import com.bankmandiri.streamfailedpe.model.Request;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public List<KafkaConsumerResponse> getConsumerIds() {
        List<KafkaConsumerResponse> consumers = new ArrayList<>();
        consumers = customKafkaContainerRegistration.getAllIds()
                .stream()
                .map(this::createKafkaConsumerResponse)
                .collect(Collectors.toList());
        logger.info(consumers.toString());
        return consumers;
    }

    @PostMapping(path = "/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createConsumer(@RequestBody Request request) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(request.getConsumerId());
        if (Objects.isNull(listenerContainer)) {
            try{
                customKafkaContainerRegistration.registerCustomKafkaContainer(request);
                logger.info("Consumer with id %s created " + request.getConsumerId());
            }catch (Exception e){
                logger.info("Consumer with id %s not created yet" + request.getConsumerId());
                throw new RuntimeException(String.format("Consumer with id %s is not created yet", request.getConsumerId()));
            }
        } else {
            logger.info("consumer with id %s already created" + request.getConsumerId());
            throw new RuntimeException(String.format("Consumer with id %s is already created", request.getConsumerId()));
        }
    }

    @PostMapping(path = "/activate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void activateConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            throw new RuntimeException(String.format("Consumer with id %s is not found", consumerId));
        } else if (listenerContainer.isRunning()) {
            throw new RuntimeException(String.format("Consumer with id %s is already running", consumerId));
        } else {
            logger.info("Running a consumer with id " + consumerId);
            listenerContainer.start();
        }
    }

    @PostMapping(path = "/deactivate")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void deactivateConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            throw new RuntimeException(String.format("Consumer with id %s is not found", consumerId));
        } else if (!listenerContainer.isRunning()) {
            throw new RuntimeException(String.format("Consumer with id %s is already stop", consumerId));
        } else {
            logger.info("Stopping a consumer with id " + consumerId);
            listenerContainer.stop();
        }
    }

    @PostMapping(path = "/pause")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void pauseConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            throw new RuntimeException(String.format("Consumer with id %s is not found", consumerId));
        } else if (!listenerContainer.isRunning()) {
            throw new RuntimeException(String.format("Consumer with id %s is not running", consumerId));
        } else if (listenerContainer.isContainerPaused()) {
            throw new RuntimeException(String.format("Consumer with id %s is already paused", consumerId));
        } else if (listenerContainer.isPauseRequested()) {
            throw new RuntimeException(String.format("Consumer with id %s is already requested to be paused", consumerId));
        } else {
            logger.info("Pausing a consumer with id " + consumerId);
            listenerContainer.pause();
        }
    }

    @PostMapping(path = "/resume")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void resumeConsumer(@RequestBody String consumerId) {
        MessageListenerContainer listenerContainer = customKafkaContainerRegistration.getContainer(consumerId);
        if (Objects.isNull(listenerContainer)) {
            throw new RuntimeException(String.format("Consumer with id %s is not found", consumerId));
        } else if (!listenerContainer.isRunning()) {
            throw new RuntimeException(String.format("Consumer with id %s is not running", consumerId));
        } else if (!listenerContainer.isContainerPaused()) {
            throw new RuntimeException(String.format("Consumer with id %s is not paused", consumerId));
        } else {
            logger.info("Resuming a consumer with id " + consumerId);
            listenerContainer.resume();
        }
    }

    private KafkaConsumerResponse createKafkaConsumerResponse(String consumerId) {
        MessageListenerContainer listenerContainer =
                customKafkaContainerRegistration.getContainer(consumerId);
        return KafkaConsumerResponse.builder()
                .consumerId(consumerId)
                .groupId(listenerContainer.getGroupId())
                .active(listenerContainer.isRunning())
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
