package com.bankmandiri.streamfailedpe.container;
import com.bankmandiri.streamfailedpe.model.Request;
import com.bankmandiri.streamfailedpe.services.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomKafkaContainerRegistration {

    @Autowired
    public CustomConsumerFactory customConsumerFactory;

    @Autowired
    private StreamService streamService;

    @Value("${concurrent.consumer.kafka}")
    private int concurrentConsumer;

    @Value("${json.key.url}")
    private String jsonKeyUrl;

    @Value("${aes.secret.key}")
    private String secretKey;

    @Value("${init.retry.after.failed}")
    private int initRetryAfterFailed;

    @Value("${retry.count}")
    private int retryCount;

    @Value("${dir.failed.pe}")
    private String dirFailedPE;

    private final Map<String, MessageListenerContainer> registry = new ConcurrentHashMap<>();

    public void registerCustomKafkaContainer(Request request) {
        CustomContainerProperties customContainerProperties = new CustomContainerProperties(
                request.getTopicName(),request.getConsumerId(),
                streamService,jsonKeyUrl,secretKey,initRetryAfterFailed,retryCount,dirFailedPE);
        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(
                customConsumerFactory.getCustomConsumerFactory(),
                customContainerProperties.getContainerProperties());
        container.setConcurrency(concurrentConsumer);
        container.setAutoStartup(request.getConsumerActivation());
        container.setAlwaysClientIdSuffix(true);
        this.registry.put(request.getConsumerId(), container);
        container.start();
    }

    public MessageListenerContainer getContainer(String consumerId) {
        return this.registry.get(consumerId);
    }

    public void removeContainer(String consumerId) {
        this.registry.remove(consumerId);
    }

    public void removeAllContainer(){
        this.registry.clear();
    }

    public Set<String> getAllIds(){
        return new HashSet<>(registry.keySet());
    }
}
