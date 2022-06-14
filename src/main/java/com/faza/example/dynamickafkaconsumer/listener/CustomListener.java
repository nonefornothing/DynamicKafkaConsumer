package com.faza.example.dynamickafkaconsumer.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;

@Slf4j
public class CustomListener implements AcknowledgingMessageListener<String,String>{


    /**
     * Invoked with data from kafka.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     */
//    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment) {
        log.info("Data received : " + data.value());
        acknowledgment.acknowledge();
        log.info("Data consumed: " + data);
    }
}
