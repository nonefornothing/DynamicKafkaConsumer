package com.faza.example.dynamickafkaconsumer.container;
import com.faza.example.dynamickafkaconsumer.model.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
public class CustomKafkaContainerRegistration {

    @Value("${concurrent.consumer.kafka}")
    private int concurrentConsumer;

    @Autowired
    public CustomConsumerFactory customConsumerFactory;

    public void registerCustomKafkaContainer(Request request) {
        CustomContainerProperties customContainerProperties = new CustomContainerProperties(request.getTopicName(),request.getConsumerId());
        ConcurrentMessageListenerContainer<String, String> container = new ConcurrentMessageListenerContainer<>(
                customConsumerFactory.getCustomConsumerFactory(),
                customContainerProperties.getContainerProperties());
        container.setConcurrency(concurrentConsumer);
        container.setAutoStartup(request.getConsumerActivation());
        container.getContainers();
        container.setBeanName(request.getConsumerId());
        container.start();
    }


}
