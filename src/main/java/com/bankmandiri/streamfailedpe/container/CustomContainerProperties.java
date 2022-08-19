package com.bankmandiri.streamfailedpe.container;

import com.bankmandiri.streamfailedpe.listener.CustomListener;
import com.bankmandiri.streamfailedpe.services.StreamService;
import org.springframework.kafka.listener.ContainerProperties;

public class CustomContainerProperties {

    private final String topic;
    private final String consumerId;
    private final StreamService streamService;
    private final String jsonKeyUrl;
    private final String secretKey;
    private final int initRetryAfterFailed;
    private final int retryCount;
    private final String dirFailedPE;


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

    public ContainerProperties getContainerProperties() {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setClientId(consumerId);
        containerProperties.setGroupId(consumerId);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new CustomListener(streamService,jsonKeyUrl,secretKey,initRetryAfterFailed,retryCount,dirFailedPE));
        return containerProperties;
    }

}
