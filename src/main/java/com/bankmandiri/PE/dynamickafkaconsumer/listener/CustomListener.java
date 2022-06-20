package com.bankmandiri.PE.dynamickafkaconsumer.listener;

import com.bankmandiri.PE.dynamickafkaconsumer.services.StreamService;
import com.bankmandiri.PE.dynamickafkaconsumer.utils.AESUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import java.io.Serializable;
import java.text.SimpleDateFormat;

public class CustomListener implements AcknowledgingMessageListener<String,String> , Serializable {

    /**
     *
     * consumer engine clean topic PE
     * - construct and decode message to get URL from message
     * - send message to destination
     *
     */

    private final Logger logger = LoggerFactory.getLogger(CustomListener.class);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @Autowired
    private StreamService streamService;

    @Value("${json.key.url}")
    private String jsonKeyUrl;

    @Value("${aes.secret.key}")
    private String secretKey;

    /**
     * Invoked with data from kafka.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     */

    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment){

        String bodyReal =null;
        String result = null;

        try {
            bodyReal = getBody(data);
            String encryptedData = encryptData(bodyReal);
            String uri =getUri(data);
            logger.info("start send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedData);
            result = sending(encryptedData,uri);
            acknowledgment.acknowledge();
            logger.info("end send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition() + " | response : " + result);
        }catch (Exception e) {
            logger.error("Error....!!!  " + e.getMessage() +" | Offset  : " + data.offset() + " | partition : " + data.partition());
        }
    }

    private String sending(String encryptedData,String uri) throws Exception {
        String body =null;
        body = "{\"data\":\""+encryptedData+"\"}";
        String result = streamService.sendData(body,uri);
        logger.info("response PE : "+ sdf.format(new java.util.Date()) +" ==> " + result);
        return result;
    }

    public String getBody(ConsumerRecord<String, String> consumerRecord) throws JsonMappingException, JsonProcessingException {
        String bodyReal =null;
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value());
        parseMessages(node);
        bodyReal = node.toString();
        return bodyReal;
    }

    private String getUri (ConsumerRecord<String, String> consumerRecord) throws JsonMappingException, JsonProcessingException {
        String uri =null;
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value()) ;
        uri = getUri(node);
        return uri;

    }

    private String encryptData(String bodyReal) {
        String encryptedData = AESUtils.encrypt(bodyReal, secretKey);
        return encryptedData;
    }

    private ObjectNode parseMessages(ObjectNode node) {
        try {
            node.remove(jsonKeyUrl);
        } catch (Exception e) {
            logger.error("Error parsing message....!!! [ " + node.asText() + " ]" + e.getMessage());
        }
        return node;
    }

    private String getUri(ObjectNode node) {
        String uri ="";
        try {
            uri = node.get(jsonKeyUrl).asText();
        } catch (Exception e) {
            logger.error("Error get uri from message....!!! [ "+node.asText()+" ]"+  e.getMessage());
        }
        return uri;
    }

}
