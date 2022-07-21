package com.bankmandiri.streamfailedpe.listener;

import com.bankmandiri.streamfailedpe.services.StreamService;
import com.bankmandiri.streamfailedpe.utils.AESUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.listener.AcknowledgingMessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.web.client.HttpClientErrorException;


import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

public class CustomListener implements AcknowledgingMessageListener<String,String> , Serializable {

    /**
     *
     * consumer engine clean topic PE
     * - construct and decode message to get URL from message
     * - send message to destination
     * - retry scenario (exponential time) with X maximum time and if still failed then its loop forever until its success
     *   ex : retryCount = 5 ,
     *   sleep 1 s
     *   sleep 2 s
     *   sleep 3 s
     *   sleep 4 s
     *   sleep 5 s
     *   looping forever with 5 s sleep
     *
     */

    private final StreamService streamService;
    private final String jsonKeyUrl;
    private final String secretKey;
    private final int initRetryAfterFailed;
    private final int retryCount;
    private final String dirFailedPE;

    public CustomListener(StreamService streamService, String jsonKeyUrl, String secretKey, int initRetryAfterFailed, int retryCount, String dirFailedPE) {
        this.streamService = streamService;
        this.jsonKeyUrl = jsonKeyUrl;
        this.secretKey = secretKey;
        this.initRetryAfterFailed = initRetryAfterFailed;
        this.retryCount = retryCount;
        this.dirFailedPE = dirFailedPE;
    }

    private static final long serialVersionUID = 3938181625248309928L;

    private final Logger logger = LoggerFactory.getLogger(CustomListener.class);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    /**
     * Invoked with data from kafka.
     *
     * @param consumerRecord the data to be processed.
     * @param acknowledgment the acknowledgment.
     */

    @Override
    public void onMessage(@NotNull ConsumerRecord<String,String> consumerRecord, Acknowledgment acknowledgment){

        String encryptedMessage = null;
        String uriDestination = null;
        String bodyReal = null;
        ResponseEntity<String> result;
        try {
            try {
                bodyReal = getBody(consumerRecord,jsonKeyUrl);
                encryptedMessage = encryptMessage(bodyReal);
                uriDestination =getUri(consumerRecord);
            }catch (Exception e){
                writeToFile(consumerRecord.value());
                logger.error("Error....!!!" + e.getMessage());
                logger.error("write data with offset " + consumerRecord.offset() + " , partition " + consumerRecord.partition() + " to file");
            }
            if (bodyReal != null) {
                logger.info("start send data : " + sdf.format(new java.util.Date()) + " | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | data : " + bodyReal);
                result = sending(encryptedMessage, uriDestination);
                acknowledgment.acknowledge();
                logger.info("end send data : " + sdf.format(new java.util.Date()) + " | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | response : " + result.getBody());
            }
        }catch (HttpClientErrorException ec){
            if(ec.getStatusCode().value() == 400 || ec.getStatusCode().value() == 406){
                if (bodyReal != null) {
                    writeToFile(bodyReal);
                    logger.error("Error....!!! error message " + ec.getMessage() + " status code : " + ec.getStatusCode());
                    logger.error("write data | " + bodyReal + " with offset " + consumerRecord.offset() + " , partition " + consumerRecord.partition() + " to file");
                }else{
                    logger.error("Body null");
                }
                acknowledgment.acknowledge();
            }else {
                while(true){
                    try {
                        TimeUnit.MILLISECONDS.sleep(5000);
                        logger.info("start resend data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedMessage);
                        result = sending(encryptedMessage,uriDestination);
                        acknowledgment.acknowledge();
                        logger.info("end resend data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | response : " + result);
                        break;
                    }catch (Exception e1) {
                        logger.error("Error retry forever ....!!! ==> " + " | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | error message : " + e1.getMessage());
                    }
                }
            }
        }catch (Exception e){
            logger.error("Error....!!!  " + e.getMessage());
            logger.error("Retry after "+initRetryAfterFailed+" ms ....!!!");
            try {
                TimeUnit.MILLISECONDS.sleep(initRetryAfterFailed);
                int i;
                for (i = 2; i <= retryCount;i++) {
                    try {
                        logger.info("start retry send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition()+" | data : "+ bodyReal);
                        result = sending(encryptedMessage,uriDestination);
                        acknowledgment.acknowledge();
                        logger.info("end retry send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | response : " + result.getBody());
                        break;
                    }catch (HttpClientErrorException ec){
                        if (bodyReal != null) {
                            writeToFile(bodyReal);
                            logger.error("Error....!!! error message " + ec.getMessage() + " status code : " + ec.getStatusCode());
                            logger.error("write data | " + bodyReal + " with offset " + consumerRecord.offset() + " , partition " + consumerRecord.partition() + " to file");
                        }else{
                            logger.error("Body null");
                        }
                        acknowledgment.acknowledge();
                    }catch (Exception e1) {
                        TimeUnit.MILLISECONDS.sleep((long) initRetryAfterFailed *i );
                        logger.error("Error retry ....!!! ==> " + i + " "+" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | error message : " + e1.getMessage());
                    }
                }
                if (i>retryCount){
                    while(true){
                        try {
                            logger.info("start send data to PE : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition()+" | data : "+ bodyReal + " | encryptedData : " + encryptedMessage);
                            result = sending(encryptedMessage,uriDestination);
                            acknowledgment.acknowledge();
                            logger.info("end send data : "+ sdf.format(new java.util.Date()) +" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | response : " + result);
                            break;
                        }catch (HttpClientErrorException ec){
                            if (bodyReal != null) {
                                writeToFile(bodyReal);
                                logger.error("Error....!!! error message " + ec.getMessage() + " status code : " + ec.getStatusCode());
                                logger.error("write data | " + bodyReal + " with offset " + consumerRecord.offset() + " , partition " + consumerRecord.partition() + " to file");
                            }else{
                                logger.error("Body null");
                            }
                            acknowledgment.acknowledge();
                        }catch (Exception e1) {
                            logger.error("Error retry forever ....!!! ==> " + " | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | error message : " + e1.getMessage());
                            continue;
                        }
                        TimeUnit.MILLISECONDS.sleep((long) initRetryAfterFailed *i);
                    }
                }
            } catch (Exception e3) {
                logger.error("Error while retry data !!!!"+" | Offset  : " + consumerRecord.offset() + " | partition : " + consumerRecord.partition() + " | error message : " + e3.getMessage());
            }
        }
    }

    private ResponseEntity<String> sending(String encryptedMessage, String uri) throws Exception {
        String body;
        body = "{\"data\":\""+encryptedMessage+"\"}";
        return streamService.sendData(body,uri);
    }

    private String encryptMessage(String bodyReal) {
        return AESUtils.encrypt(bodyReal, secretKey);
    }

    private void parseMessages(ObjectNode node, String jsonKeyUrl) {
        try {
            node.remove(jsonKeyUrl);
        } catch (Exception e) {
            logger.error("Error parsing message....!!! [ " + node.asText() + " ]" + e.getMessage());
        }
    }

    public String getBody(ConsumerRecord<String, String> consumerRecord,String jsonKeyUrl) throws JsonProcessingException {
        String bodyReal;
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value());
        parseMessages(node,jsonKeyUrl);
        bodyReal = node.toString();
        return bodyReal;
    }

    private String getUri (ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        ObjectNode node = (ObjectNode) new ObjectMapper().readTree(consumerRecord.value()) ;
        return getUri(node);
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

    private void writeToFile(String bodyReal) {
        try {
            if(!bodyReal.isEmpty()) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                Files.write(Paths.get(dirFailedPE+sdf1.format(new java.util.Date())+ "-DLQ-PE.txt"), (bodyReal+ System.lineSeparator()).getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            }
            else{
                logger.error("Error...!!! write to failed PE file ...!!! Data null" );
            }
        }catch (IOException e3) {
            logger.error("Error...!!! write to failed PE file ...!!!" + e3.getMessage());
        }
    }
}
