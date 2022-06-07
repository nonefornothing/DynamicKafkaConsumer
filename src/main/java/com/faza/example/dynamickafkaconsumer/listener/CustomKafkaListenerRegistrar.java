package com.faza.example.dynamickafkaconsumer.listener;

import com.faza.example.dynamickafkaconsumer.configuration.CustomKafkaListenerProperties;
import com.faza.example.dynamickafkaconsumer.model.CustomKafkaListenerProperty;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomKafkaListenerRegistrar implements InitializingBean {
    private final CustomKafkaListenerProperties customKafkaListenerProperties;
    private final BeanFactory beanFactory;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    private final KafkaListenerContainerFactory kafkaListenerContainerFactory;

    @Override
    public void afterPropertiesSet() {
        customKafkaListenerProperties.getListeners()
                .forEach(this::registerCustomKafkaListener);
    }

    public void CustomKafkaListenerRegistrar(){

    }

    public void onMessage(){

    }

    public void registerCustomKafkaListener(String name, CustomKafkaListenerProperty customKafkaListenerProperty) {
        this.registerCustomKafkaListener(name, customKafkaListenerProperty, false);
    }


    public void registerCustomKafkaListener(String name, CustomKafkaListenerProperty customKafkaListenerProperty,
                                            boolean startImmediately) {
        try{
            String listenerClass = String.join(".", CustomKafkaListenerRegistrar.class.getPackageName(),
                    customKafkaListenerProperty.getListenerClass());
            CustomMessageListener customMessageListener = (CustomMessageListener) beanFactory.getBean(Class.forName(listenerClass));
            kafkaListenerEndpointRegistry.registerListenerContainer(
                    customMessageListener.createKafkaListenerEndpoint(name, customKafkaListenerProperty.getTopic()),
                    kafkaListenerContainerFactory, startImmediately);
        }catch (ClassNotFoundException | NoSuchMethodException e){
            throw new RuntimeException(e);
        }
    }
}
