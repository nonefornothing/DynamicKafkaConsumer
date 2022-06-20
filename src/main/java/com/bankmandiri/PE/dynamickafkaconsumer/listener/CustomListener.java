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

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class CustomListener implements AcknowledgingMessageListener<String,String> , Serializable {

    /**
     *
     * consumer engine clean topic PE
     * - construct and decode message to get URL from message
     * - send message to destination
     * - retry re send after failed send until < retry.count
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
        logger.info("Data received : " + data.value());
        acknowledgment.acknowledge();
        logger.info("Data consumed: " + data);



        String bodyReal =null;
        try {
            bodyReal = getBody(data);
            String decript = decript(bodyReal);
            String uri =getUri(data);
            logger.info("start send data mansek : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ bodyReal);
            logger.info("start send data mansek decript : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ decript);
            sending(decript,uri);
            acknowledgment.acknowledge();
            logger.info("end send data mansek : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition());
        }catch (Exception e) {
            logger.error("Error....!!!" + e.getMessage());
            logger.error("write to file...!!!"+" | Offset  : " + data.offset() + " | partition : " + data.partition());
        }
    }

    private String sending(String decript,String uri) throws Exception {
        String body =null;
        body = "{\"data\":\""+decript+"\"}";
        String reslut = streamService.sendData(body,uri);
        logger.info("response mansek : "+ sdf.format(new java.util.Date()) +" ==> " + reslut);
        return reslut;
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

    private String decript(String bodyReal) {
        String decript = AESUtils.encrypt(bodyReal, secretKey);
        return decript;
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
