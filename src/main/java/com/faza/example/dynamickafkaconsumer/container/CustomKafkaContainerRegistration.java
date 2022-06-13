package com.faza.example.dynamickafkaconsumer.container;

import com.faza.example.dynamickafkaconsumer.configuration.CustomKafkaListenerProperties;
import com.faza.example.dynamickafkaconsumer.listener.CustomListener;
import com.faza.example.dynamickafkaconsumer.listener.CustomMessageListener;
import com.faza.example.dynamickafkaconsumer.model.CustomKafkaListenerProperty;
import lombok.SneakyThrows;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.stereotype.Component;



import java.util.HashMap;
import java.util.Map;

@Component
public class KafkaContainerRegistration implements InitializingBean {

    @Value("${kafka.consumer.broker}")
    private String bootstrapServers;

    @Value("${kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset;

    @Value("${enable.auto.commit}")
    private String enableAutoCommit;

    @Autowired
    private CustomKafkaListenerProperties customKafkaListenerProperties;

    private ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private ConcurrentMessageListenerContainer container;
    private ConsumerFactory consumerFactoryStringCustom;

    @Bean
    public ConsumerFactory<String,String> consumerFactoryStringCustom() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.MAX_VALUE);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer());
    }

    public void registerCustomKafkaContainer(String topic,String consumerId,Boolean activation, Integer concurrency) {

        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setClientId(consumerId);
        containerProperties.setGroupId(consumerId);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new CustomListener());

        ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer(consumerFactoryStringCustom , containerProperties);
        container.setConcurrency(concurrency);
        container.setAutoStartup(activation);
        container.start();
    }


    @Override
    public void afterPropertiesSet() {
        customKafkaListenerProperties.getListeners()
                .forEach(this::registerCustomKafkaContainer);
    }

    private void registerCustomKafkaContainer(String s, CustomKafkaListenerProperty customKafkaListenerProperty) {
    }

    private void registerCustomKafkaContainer(String topic, String consumerId) {
        this.registerCustomKafkaContainer(topic,consumerId,true,6);
    }


}
