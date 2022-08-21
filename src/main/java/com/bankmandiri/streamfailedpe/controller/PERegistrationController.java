package com.bankmandiri.streamfailedpe.controller;

import com.bankmandiri.streamfailedpe.validator.RequestValidator;
import com.bankmandiri.streamfailedpe.model.ConsumerData;
import com.bankmandiri.streamfailedpe.model.Status;
import com.bankmandiri.streamfailedpe.services.ElasticService;
import com.bankmandiri.streamfailedpe.utils.CustomHeader;
import com.bankmandiri.streamfailedpe.utils.ErrorCodeEnum;

import org.elasticsearch.ElasticsearchStatusException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;



/**
 *
 * This controller for maintain list Tran code
 * - Create
 * - Read
 * - Update
 * - Delete
 *
 */

@RestController
@RequestMapping(path = "/pe-management")
public class PERegistrationController implements ErrorController {

    private final Logger logger = LoggerFactory.getLogger(PERegistrationController.class);

    @Autowired
    private ElasticService elasticService;

    @GetMapping
    public ResponseEntity<List<ConsumerData>> getAllPEData(){
        List<ConsumerData> result = null;
        try{
            result = elasticService.getListConsumerData();
            logger.info("registered consumer data : " + result.toString());
        }catch (Exception e){
            logger.error("Error while get all data from index");    
        }

        return ResponseEntity.ok()
                .headers(CustomHeader.setHeaders())
                .body(result);
    }

    @PostMapping("/create")
    public ResponseEntity<Status> createPEData(@RequestBody ConsumerData consumerData) {
        Status sts = new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        try {
            sts = RequestValidator.validate(consumerData);
            if(sts.getConsumerName() != null && !sts.getConsumerTopic().isEmpty()) {
                logger.info("Client request invalid data || " + sts);
                return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.BAD_REQUEST);
            }
            sts.setConsumerName(consumerData.getConsumerName());
            sts.setConsumerTopic(consumerData.getConsumerTopic());
            sts.setResponse_timestamp(sdf.format(new Date()));
            try {
                ConsumerData consumerResult = elasticService.getById(consumerData.getConsumerName());
                if(consumerResult == null || consumerResult.getConsumerName() == null || consumerResult.getConsumerName().isEmpty()) {
                    try {
                        String result = elasticService.insert(consumerData);
                        if (result.equalsIgnoreCase("CREATED")) {
                            sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                            sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                            logger.info("Success for add data to elasticSearch || " + sts);
                        }else {
                            sts.setResponses_code(ErrorCodeEnum.ADD_DATA_FAILED.getCode());
                            sts.setResponse_message(ErrorCodeEnum.ADD_DATA_FAILED.getDefaultMsg());
                            logger.error("error while add consumerData to elasticSearch : " + sts);
                        }
                    } catch (Exception e) {
                        sts.setResponses_code(ErrorCodeEnum.ADD_DATA_FAILED.getCode());
                        sts.setResponse_message(ErrorCodeEnum.ADD_DATA_FAILED.getDefaultMsg());
                        logger.error("Error while insert data to elasticSearch || " + sts);
                    }
                }else {
                    sts.setResponses_code(ErrorCodeEnum.DATA_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.DATA_EXIST.getDefaultMsg());
                    logger.info("Data Exist in ElasticSearch || " + sts);
                }
            }
            catch (ElasticsearchStatusException e) {
                sts.setResponses_code(ErrorCodeEnum.INDEX_NOT_EXIST.getCode());
                sts.setResponse_message(ErrorCodeEnum.INDEX_NOT_EXIST.getDefaultMsg());
                logger.error("index not exist || " + sts);
            }
        } catch (Exception e) {
            sts.setResponses_code(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
            sts.setResponse_message(ErrorCodeEnum.UNKNOWN_ERROR.getDefaultMsg());
            logger.error("Error while validate request || " + sts);
        }
        return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Status> updatePEData(@PathVariable String consumerName,@RequestBody ConsumerData consumerData){
        Status sts= new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        try{
            sts = RequestValidator.validate(consumerData);
            if(sts.getConsumerName() != null && !sts.getConsumerTopic().isEmpty()) {
                logger.info("Client request invalid data || " + sts);
                return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.BAD_REQUEST);
            }
            sts.setConsumerName(consumerData.getConsumerName());
            sts.setConsumerTopic(consumerData.getConsumerTopic());
            sts.setResponse_timestamp(sdf.format(new Date()));
            try{
                ConsumerData peResult = elasticService.getById(consumerName);
                if(peResult != null && !peResult.getConsumerName().isEmpty()) {
                    try {
                        String isDelete = elasticService.deleteById(consumerData);
                        try {
                            String result = elasticService.insert(consumerData);
                            if(isDelete.equalsIgnoreCase("DELETED") && result.equalsIgnoreCase("CREATED")) {
                                sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                                sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                            }else {
                                sts.setResponses_code(ErrorCodeEnum.UPDATE_DATA_FAILED.getCode());
                                sts.setResponse_message(ErrorCodeEnum.UPDATE_DATA_FAILED.getDefaultMsg());
                            }
                        }catch (Exception e){
                            sts.setResponses_code(ErrorCodeEnum.UPDATE_DATA_FAILED.getCode());
                            sts.setResponse_message(ErrorCodeEnum.UPDATE_DATA_FAILED.getDefaultMsg());
                            logger.error("Error while insert data || " + consumerData.getConsumerName());
                        }
                    }catch (Exception e){
                        sts.setResponses_code(ErrorCodeEnum.DELETE_DATA_FAILED.getCode());
                        sts.setResponse_message(ErrorCodeEnum.DELETE_DATA_FAILED.getDefaultMsg());
                        logger.error("Error while delete data in elasticSearch || " + sts);
                    }
                }else {
                    sts.setResponses_code(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getDefaultMsg());
                    sts.setConsumerName(consumerData.getConsumerName());
                    sts.setConsumerTopic(consumerData.getConsumerTopic());
                }
            }catch (Exception e){
                sts.setResponses_code(ErrorCodeEnum.INDEX_NOT_EXIST.getCode());
                sts.setResponse_message(ErrorCodeEnum.INDEX_NOT_EXIST.getDefaultMsg());
                logger.error("index not exist || " + sts);
            }
        }catch (Exception e){
            sts.setResponses_code(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
            sts.setResponse_message(ErrorCodeEnum.UNKNOWN_ERROR.getDefaultMsg());
            logger.error("Error while validate request || " + sts);
        }
        return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.CREATED);
    }

    @PostMapping("/delete")
    public ResponseEntity<Status> deletePEData(@RequestBody ConsumerData consumerData){
        Status sts = new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        try{
            sts = RequestValidator.validate(consumerData);
            if(sts.getConsumerName() != null && !sts.getConsumerTopic().isEmpty()) {
                logger.info("Client request invalid data || " + sts);
                return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.BAD_REQUEST);
            }
            sts.setConsumerName(consumerData.getConsumerName());
            sts.setConsumerTopic(consumerData.getConsumerTopic());
            sts.setResponse_timestamp(sdf.format(new Date()));
            try {
                ConsumerData result = elasticService.getById(consumerData.getConsumerName());
                if (result == null || result.getConsumerName() == null || result.getConsumerName().isEmpty()) {
                    try{
                        String isDelete = elasticService.deleteById(consumerData);
                        if (isDelete.equalsIgnoreCase("DELETED")) {
                            sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                            sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                            logger.info("Success for add data to elasticSearch || " + sts);
                        } else if (isDelete.equalsIgnoreCase("NOT_FOUND")) {
                            sts.setResponses_code(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getCode());
                            sts.setResponse_message(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getDefaultMsg());
                            logger.info("ConsumerData not found in elasticSearch || " + sts);
                        } else {
                            sts.setResponses_code(ErrorCodeEnum.DELETE_DATA_FAILED.getCode());
                            sts.setResponse_message(ErrorCodeEnum.DELETE_DATA_FAILED.getDefaultMsg());
                            logger.error("Error while delete data in elasticSearch || " + sts);
                        }
                    }catch (Exception e){
                        sts.setResponses_code(ErrorCodeEnum.DELETE_DATA_FAILED.getCode());
                        sts.setResponse_message(ErrorCodeEnum.DELETE_DATA_FAILED.getDefaultMsg());
                        logger.error("Error while delete data in elasticSearch || " + sts);
                    }
                } else {
                    sts.setResponses_code(ErrorCodeEnum.DATA_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.DATA_EXIST.getDefaultMsg());
                    logger.info("Data Exist in ElasticSearch || " + sts);
                }
            }
            catch (Exception e){
                sts.setResponses_code(ErrorCodeEnum.INDEX_NOT_EXIST.getCode());
                sts.setResponse_message(ErrorCodeEnum.INDEX_NOT_EXIST.getDefaultMsg());
                logger.error("index not exist || " + sts);
            }
        }catch (Exception e){
            sts.setResponses_code(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
            sts.setResponse_message(ErrorCodeEnum.UNKNOWN_ERROR.getDefaultMsg());
            logger.error("Error while validate request || " + sts);
        }
        return new ResponseEntity<>(sts,CustomHeader.setHeaders(),HttpStatus.OK);
    }

}
