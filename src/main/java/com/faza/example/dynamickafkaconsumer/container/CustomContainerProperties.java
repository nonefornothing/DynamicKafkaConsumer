package com.faza.example.dynamickafkaconsumer.container;

import com.faza.example.dynamickafkaconsumer.listener.CustomListener;

public class ContainerProperties {

    public org.springframework.kafka.listener.ContainerProperties containerProperties(String topic) {
//        ContainerProperties containerProperties = new ContainerProperties(topic);
//        containerProperties.setMessageListener(new MyListener());
//        containerProperties.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
//

        org.springframework.kafka.listener.ContainerProperties containerProperties = new org.springframework.kafka.listener.ContainerProperties(topic);
        containerProperties.setClientId("consumertesting");
        containerProperties.setGroupId("consumertesting");
        containerProperties.setAckMode(org.springframework.kafka.listener.ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new CustomListener());
        return containerProperties;
    }

}
