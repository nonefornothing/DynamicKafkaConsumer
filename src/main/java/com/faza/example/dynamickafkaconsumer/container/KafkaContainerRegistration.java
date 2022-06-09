package com.faza.example.dynamickafkaconsumer.container;

import com.faza.example.dynamickafkaconsumer.listener.CustomListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.BatchAcknowledgingMessageListener;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

public class KafkaContainerRegistration {

    @Value("${kafka.consumer.broker}")
    private String bootstrapServers;

    @Value("${kafka.consumer.id.group}")
    private String idGroupConsumer;

    @Value("${kafka.consumer.auto.offset.reset}")
    private String autoOffsetReset;

    @Value("${kafka.schema.registry.url}")
    private String schemaRegistryUrl;

    @Value("${enable.auto.commit}")
    private String enableAutoCommit;

    @Value("${concurrent.consumer.kafka}")
    private int concurrentConsumer;

    private ConcurrentKafkaListenerContainerFactory<String, String> factory;
    private ConcurrentMessageListenerContainer container;
    private ConsumerFactory consumerFactoryStringCustom;

    KafkaContainerRegistration(String topic, String consumerId){
        this.registerListenerContainer(topic,consumerId,consumerId,true,3);
    }

    @Bean
    public ConsumerFactory<String,String> consumerFactoryStringCustom() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, idGroupConsumer);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, Integer.MAX_VALUE);
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new StringDeserializer());
    }

    public void registerListenerContainer(String topic,String consumerId,String groupId,Boolean activation, int concurrency) {

        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setClientId(consumerId);
        containerProperties.setGroupId(groupId);
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        containerProperties.setMessageListener(new CustomListener());

        ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer(consumerFactoryStringCustom , containerProperties);
        container.setConcurrency(concurrency);
        container.setAutoStartup(activation);
        container.start();
    }



}
