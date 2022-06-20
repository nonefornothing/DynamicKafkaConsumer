package com.bankmandiri.PE.dynamickafkaconsumer.listener;

import com.bankmandiri.PE.dynamickafkaconsumer.services.StreamService;
import com.bankmandiri.PE.dynamickafkaconsumer.utils.AESUtils;
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
import java.util.concurrent.TimeUnit;

public class CustomListener implements AcknowledgingMessageListener<String,String> , Serializable {

    /**
     *
     * consumer engine clean topic PE
     * - construct and decode message to get URL from message
     * - send message to destination
     * - retry scenerio (exponential time) with X maximum time and if still failed then its loop forever untill its success
     *   ex : retryCount = 5 ,
     *   sleep 1 s
     *   sleep 2 s
     *   sleep 3 s
     *   sleep 4 s
     *   sleep 5 s
     *   looping forever with 5 s sleep
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

    @Value("${init.retry.after.failed}")
    private int initRetryAfterFailed;

    @Value("${retry.count}")
    private int retryCount;

    /**
     * Invoked with data from kafka.
     *
     * @param data           the data to be processed.
     * @param acknowledgment the acknowledgment.
     */

    @Override
    public void onMessage(ConsumerRecord data, Acknowledgment acknowledgment){

        String result = null;
        String bodyReal = getBody(data);
        String encryptedData = encryptData(bodyReal);
        String uri = getUri(data);

        if (bodyReal == null || encryptedData == null || uri == null){
            logger.error("Error while get needed data | " + " bodyReal = " + bodyReal + " | encryptedData = " + encryptedData + " | Uri = " + uri);
        }else{

            try {
                logger.info("start send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedData);
                result = sending(encryptedData,uri);
                acknowledgment.acknowledge();
                logger.info("end send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition() + " | response : " + result);
            }catch (Exception e) {
                logger.error("Error....!!!" + e.getMessage());
                logger.error("Retry after "+initRetryAfterFailed+" ms ....!!!");
                try {
                    TimeUnit.MILLISECONDS.sleep(initRetryAfterFailed);
                    int i;
                    for (i = 2; i <= retryCount;i++) {
                        try {
                            logger.info("start send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedData);
                            result = sending(encryptedData,uri);
                            acknowledgment.acknowledge();
                            logger.info("end send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition() + " | response : " + result);
                            break;
                        } catch (Exception e1) {
                            TimeUnit.MILLISECONDS.sleep((long) initRetryAfterFailed *i );
                            logger.error("Error retry ....!!! ==> "+i+" "+" | Offset  : " + data.offset() + " | partition : " + data.partition() + e1.getMessage());
                            continue;
                        }
                    }
                    if (i>retryCount){
                        while(true){
                            try {
                                logger.info("start send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedData);
                                result = sending(encryptedData,uri);
                                acknowledgment.acknowledge();
                                logger.info("end send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + data.offset() + " | partition : " + data.partition() + " | response : " + result);
                                break;
                            }catch (Exception e2){
                                logger.error("Error retry forever ....!!! ==> " + " | Offset  : " + data.offset() + " | partition : " + data.partition() + e2.getMessage());
                            }
                            TimeUnit.MILLISECONDS.sleep((long) initRetryAfterFailed *i);
                        }
                    }
                } catch (Exception e3) {
                    logger.error("Error while retry data !!!!"+" | Offset  : " + data.offset() + " | partition : " + data.partition() + e3.getMessage());
                }
            }
        }
    }

    private String sending(String encryptedData,String uri) throws Exception {
        String body =null;
        body = "{\"data\":\""+encryptedData+"\"}";
        String result = streamService.sendData(body,uri);
        logger.info("response PE : "+ sdf.format(new java.util.Date()) +" ==> " + result);
        return result;
    }

    public String getBody(ConsumerRecord<String, String> consumerRecord) {
        try {
            String bodyReal = null;
            ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value());
            parseMessages(node);
            bodyReal = node.toString();
            return bodyReal;
        }catch (Exception e){
            logger.error("Error while parsing body with error " + e.getMessage());
            return null;
        }
    }

    private String getUri (ConsumerRecord<String, String> consumerRecord) {
        String uri = null;
        try {
            ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value()) ;
            uri = getUri(node);
            return uri;
        }catch (Exception e){
            logger.error("Error while get Uri with error " + e.getMessage());
            return null;
        }
    }

    private String encryptData(String bodyReal) {
        try {
            String encryptedData = AESUtils.encrypt(bodyReal, secretKey);
            return encryptedData;
        }catch (Exception e){
            logger.error("Error while encrypt data with error " + e.getMessage());
            return null;
        }

    }

    private ObjectNode parseMessages(ObjectNode node) {
        try {
            node.remove(jsonKeyUrl);
        } catch (Exception e) {
            logger.error("Error parsing message....!!! [ " + node.asText() + " ]");
        }
        return node;
    }

    private String getUri(ObjectNode node) {
        String uri ="";
        try {
            uri = node.get(jsonKeyUrl).asText();
        } catch (Exception e) {
            logger.error("Error get uri from message....!!! [ "+node.asText()+" ]");
        }
        return uri;
    }
}
