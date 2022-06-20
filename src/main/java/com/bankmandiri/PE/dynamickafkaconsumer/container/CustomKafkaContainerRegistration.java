package com.bankmandiri.PE.dynamickafkaconsumer.container;
import com.bankmandiri.PE.dynamickafkaconsumer.model.Request;
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

    @Value("${concurrent.consumer.kafka}")
    private int concurrentConsumer;

    @Autowired
    public CustomConsumerFactory customConsumerFactory;

    private final Map<String, MessageListenerContainer> registry = new ConcurrentHashMap<>();

    public void registerCustomKafkaContainer(Request request) {
        CustomContainerProperties customContainerProperties = new CustomContainerProperties(request.getTopicName(),request.getConsumerId());
        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(
                customConsumerFactory.getCustomConsumerFactory(),
                customContainerProperties.getContainerProperties());
        container.setConcurrency(concurrentConsumer);
        container.setAutoStartup(request.getConsumerActivation());
        this.registry.put(request.getTopicName(), container);
        container.start();
    }

    public MessageListenerContainer getContainer(String consumerId) {
        return this.registry.get(consumerId);
    }

    public MessageListenerContainer remove(String consumerId) {
        return this.registry.remove(consumerId);
    }

    public Set<String> getAllIds(){
        Set<String> ids = new HashSet<>();
        for (String key : registry.keySet()) {
//            System.out.println(key + " : " + registry.get(key));
            ids.add(key);
        }
        return ids;
    }
}
