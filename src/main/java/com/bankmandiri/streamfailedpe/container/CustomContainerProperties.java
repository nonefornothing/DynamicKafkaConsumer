package com.bankmandiri.streamfailedpe.container;

import com.bankmandiri.streamfailedpe.listener.CustomListener;
import com.bankmandiri.streamfailedpe.services.StreamService;
import org.springframework.kafka.listener.ContainerProperties;

public class CustomContainerProperties {

    private String topic;
    private String consumerId;
    private StreamService streamService;
    private String jsonKeyUrl;
    private String secretKey;
    private int initRetryAfterFailed;
    private int retryCount;
    private String dirFailedPE;


    public CustomContainerProperties(String topic, String consumerId,StreamService streamService, String jsonKeyUrl, String secretKey ,int initRetryAfterFailed, int retryCount, String dirFailedPE) {
        this.topic = topic;
        this.consumerId = consumerId;
        this.streamService = streamService;
        this.jsonKeyUrl = jsonKeyUrl;
        this.secretKey = secretKey;
        this.initRetryAfterFailed = initRetryAfterFailed;
        this.retryCount = retryCount;
        this.dirFailedPE = dirFailedPE;
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
        containerProperties.setMessageListener(new CustomListener(streamService,jsonKeyUrl,secretKey,initRetryAfterFailed,retryCount,dirFailedPE));
        return containerProperties;
    }

}
