package com.faza.example.dynamickafkaconsumer.listener;

import com.faza.example.dynamickafkaconsumer.util.Acknowledgement;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.config.KafkaListenerEndpoint;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class MyCustomMessageListener extends CustomMessageListener {

    @Override
    public KafkaListenerEndpoint createKafkaListenerEndpoint(String name, String topic) throws NoSuchMethodException {
        MethodKafkaListenerEndpoint<String, String> kafkaListenerEndpoint =
                createDefaultMethodKafkaListenerEndpoint(name, topic);
        kafkaListenerEndpoint.setBean(new MyMessageListener());
//        kafkaListenerEndpoint.setMethod(MyMessageListener.class.getMethod("onMessage"));
        kafkaListenerEndpoint.setMethod(MyMessageListener.class.getMethod("onMessage",ConsumerRecord.class));
        return kafkaListenerEndpoint;
    }

    @Slf4j
    private static class MyMessageListener implements AcknowledgingMessageListener<String, String> {

        public void onMessage() {
            log.info("Masuk pak eko");
        }

        @Override
        public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
            log.info("My message listener got a new record: " + consumerRecord);
//            CompletableFuture.runAsync(this::sleep)
//                    .join();
//            acknowledgment.acknowledge();
            log.info("My message listener done processing record: " + consumerRecord);
        }

//        @Override
//        public void onMessage(ConsumerRecord<String, String> data) {
//            throw new UnsupportedOperationException("Container should never call this");
//        }
//
//        @Override
//        public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
//            log.info("My message listener got a new record: " + consumerRecord);
////            CompletableFuture.runAsync(this::sleep)
////                    .join();
//            acknowledgment.acknowledge();
//            log.info("My message listener done processing record: " + consumerRecord);
//        }
//    }

//    @Slf4j
//    private static class MyMessageListener implements MessageListener<String,String> {
//
//        @Override
//        public void onMessage(ConsumerRecord<String, String> consumerRecord) {
//            log.info("My message listener got a new record: " + consumerRecord);
////            CompletableFuture.runAsync(this::sleep)
////                    .join();
////            log.info("My message listener done processing record: " + consumerRecord);
//        }

//        @SneakyThrows
//        private void sleep() {
//            Thread.sleep(5000);
//        }

    }


//    @Slf4j
//    private static class MyMessageListener implements AcknowledgingMessageListener<String, String> {
//
////        @Override
////        public void onMessage(ConsumerRecord<String, String> record) {
////
////        }
//
//        @Override
//        public void onMessage(ConsumerRecord<String, String> consumerRecord, Acknowledgment acknowledgment) {
//            log.info("My message listener got a new record: " + consumerRecord);
////            CompletableFuture.runAsync(this::sleep)
////                    .join();
//            acknowledgment.acknowledge();
//            log.info("My message listener done processing record: " + consumerRecord);
//        }
//
////        @SneakyThrows
////        private void sleep() {
////            Thread.sleep(5000);
////        }
//
//    }

//    @Slf4j
//    private static class MyMessageListener implements GenericMessageListener<String, String> {
//
//        @Override
//        public void onMessage(ConsumerRecord<String, String> record) {
//            log.info("My message listener got a new record: " + record);
//            CompletableFuture.runAsync(this::sleep)
//                    .join();
//            log.info("My message listener done processing record: " + record);
//        }
//
//        @SneakyThrows
//        private void sleep() {
//            Thread.sleep(5000);
//        }
//
//        @Override
//        public void onMessage(ConsumerRecord<String, String> record, Acknowledgment acknowledgment) {
//            GenericMessageListener.super.onMessage(String.valueOf(record), acknowledgment);
//        }
//    }
}
