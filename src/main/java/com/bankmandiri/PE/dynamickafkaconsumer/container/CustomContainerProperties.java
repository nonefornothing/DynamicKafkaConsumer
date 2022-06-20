package com.bankmandiri.PE.dynamickafkaconsumer.container;

import com.bankmandiri.PE.dynamickafkaconsumer.listener.CustomListener;
import org.springframework.kafka.listener.ContainerProperties;

public class CustomContainerProperties {

    public String topic;
    public String consumerId;

    public CustomContainerProperties(String topic, String consumerId) {
        this.topic = topic;
        this.consumerId = consumerId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public ContainerProperties getContainerProperties() {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setClientId(consumerId);
        containerProperties.setGroupId(consumerId);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new CustomListener());
        return containerProperties;
    }

}
