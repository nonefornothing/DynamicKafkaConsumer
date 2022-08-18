package com.bankmandiri.streamfailedpe.controller;

import com.bankmandiri.streamfailedpe.validator.RequestValidator;
import com.bankmandiri.streamfailedpe.model.ConsumerData;
import com.bankmandiri.streamfailedpe.model.Status;
import com.bankmandiri.streamfailedpe.model.StatusList;
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
import java.util.ArrayList;
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
    public ResponseEntity<List<ConsumerData>> getAllPEData(@RequestBody List<ConsumerData> req){
        List<ConsumerData> result = null;
        try{
            result = elasticService.getListConsumerData();
        }catch (Exception e){
            logger.error("Error while get all data from index");    
        }

        return ResponseEntity.ok()
                .headers(CustomHeader.setHeaders())
                .body(result);
    }

    @PostMapping("/create")
    public ResponseEntity<Status> createPEData(@RequestBody List<ConsumerData> req) {
        Status s = new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        List<StatusList> stsList = new ArrayList<>();
        try {
            s = RequestValidator.validate(req);
            if(s.getResponse_list() != null && !s.getResponse_list().isEmpty()) {
                return new ResponseEntity(s, HttpStatus.OK);
            }
            for(ConsumerData consumerData : req){
                ConsumerData consumerResult = null;
                try {
                    consumerResult = elasticService.getById(consumerData);
                }
                catch (ElasticsearchStatusException e) {
                    logger.error("index not exist");
                }
                if(consumerResult == null || consumerResult.getConsumerName() == null || consumerResult.getConsumerName().isEmpty()) {
                    try {
                        String result = elasticService.insert(consumerData);
                        if (result.equalsIgnoreCase("CREATED")) {
                            StatusList sts = new StatusList();
                            sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                            sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                            sts.setConsumerName(consumerData.getConsumerName());
                            sts.setConsumerTopic(consumerData.getConsumerTopic());
                            stsList.add(sts);
                        } else {
                            logger.error("error while add consumerData to elasticSearch : " +  result);
                            StatusList sts = new StatusList();
                            sts.setResponses_code(ErrorCodeEnum.ADD_DATA_FAILED.getCode());
                            sts.setResponse_message(ErrorCodeEnum.ADD_DATA_FAILED.getDefaultMsg());
                            sts.setConsumerName(consumerData.getConsumerName());
                            sts.setConsumerTopic(consumerData.getConsumerTopic());
                            stsList.add(sts);
                        }
                    } catch (Exception e) {
                        logger.error("Error while insert data to elasticSearch || " + e.getMessage());
                    }
                }
                else {
                    StatusList sts = new StatusList();
                    sts.setResponses_code(ErrorCodeEnum.DATA_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.DATA_EXIST.getDefaultMsg());
                    sts.setConsumerName(consumerData.getConsumerName());
                    sts.setConsumerTopic(consumerData.getConsumerTopic());
                    stsList.add(sts);
                }
                s.setResponse_list(stsList);
                s.setResponse_timestamp(sdf.format(new Date()));
            }
        } catch (Exception e) {
            logger.error("Error while validate request || " + e.getMessage());
            StatusList sts = new StatusList();
            sts.setResponses_code(ErrorCodeEnum.UNKNOWN_ERROR.getCode());
            sts.setResponse_message(ErrorCodeEnum.UNKNOWN_ERROR.getDefaultMsg());
            stsList.add(sts);
            s.setResponse_timestamp(sdf.format(new Date()));
        }

//		return new ResponseEntity(s, HttpStatus.OK);
        return ResponseEntity.ok()
                .headers(CustomHeader.setHeaders())
                .body(s);
    }

    @PutMapping("/update")
    public ResponseEntity<Status> updatePEData(@RequestBody List<ConsumerData> req){
        Status s= new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        List<StatusList> stsList = new ArrayList<>();
        String isDelete;
        String result;

        for(ConsumerData consumerData : req){
            try{
                ConsumerData peResult = elasticService.getById(consumerData);
                if(peResult != null && !peResult.getConsumerName().isEmpty()) {
                    try {
                        isDelete = elasticService.deleteById(consumerData);
                        try {
                            result = elasticService.insert(consumerData);
                            if(isDelete.equalsIgnoreCase("DELETED") && result.equalsIgnoreCase("CREATED")) {
                                StatusList sts = new StatusList();
                                sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                                sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                                sts.setConsumerName(consumerData.getConsumerName());
                                sts.setConsumerTopic(consumerData.getConsumerTopic());
                                stsList.add(sts);
                            }else {
                                StatusList sts = new StatusList();
                                sts.setResponses_code(ErrorCodeEnum.UPDATE_DATA_FAILED.getCode());
                                sts.setResponse_message(ErrorCodeEnum.UPDATE_DATA_FAILED.getDefaultMsg());
                                sts.setConsumerName(consumerData.getConsumerName());
                                sts.setConsumerTopic(consumerData.getConsumerTopic());
                                stsList.add(sts);
                            }
                        }catch (Exception e){
                            logger.error("Error while insert data || " + consumerData.getConsumerName());
                        }
                    }catch (Exception e){
                        logger.error("Error while delete data || " + consumerData.getConsumerName());
                    }
                }else {
                    StatusList sts = new StatusList();
                    sts.setResponses_code(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getDefaultMsg());
                    sts.setConsumerName(consumerData.getConsumerName());
                    sts.setConsumerTopic(consumerData.getConsumerTopic());
                    stsList.add(sts);
                }
            }catch (Exception e){
                logger.error("Error while find data || " + consumerData.getConsumerName());
            }
            s.setResponse_list(stsList);
            s.setResponse_timestamp(sdf.format(new Date()));
        }

        return ResponseEntity.ok()
                .headers(CustomHeader.setHeaders())
                .body(s);

    }

    @PostMapping("/delete")
    public ResponseEntity<Status> deletePEData(@RequestBody List<ConsumerData> req){
        Status s= new Status();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        List<StatusList> stsList = new ArrayList<>();

        for (ConsumerData consumerData : req) {
            try {
                ConsumerData result = elasticService.getById(consumerData);
                if (result != null) {
                    try{
                        String isDelete = elasticService.deleteById(consumerData);
                        if (isDelete.equalsIgnoreCase("DELETED")) {
                            StatusList sts = new StatusList();
                            sts.setResponses_code(ErrorCodeEnum.SUCCESS.getCode());
                            sts.setResponse_message(ErrorCodeEnum.SUCCESS.getDefaultMsg());
                            sts.setConsumerName(consumerData.getConsumerName());
                            sts.setConsumerTopic(consumerData.getConsumerTopic());
                            stsList.add(sts);
                        } else if (isDelete.equalsIgnoreCase("NOT_FOUND")) {
                            StatusList sts = new StatusList();
                            sts.setResponses_code(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getCode());
                            sts.setResponse_message(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getDefaultMsg());
                            sts.setConsumerName(consumerData.getConsumerName());
                            sts.setConsumerTopic(consumerData.getConsumerTopic());
                            stsList.add(sts);
                        } else {
                            StatusList sts = new StatusList();
                            sts.setResponses_code(ErrorCodeEnum.DELETE_DATA_FAILED.getCode());
                            sts.setResponse_message(ErrorCodeEnum.DELETE_DATA_FAILED.getDefaultMsg());
                            sts.setConsumerName(consumerData.getConsumerName());
                            sts.setConsumerTopic(consumerData.getConsumerTopic());
                            stsList.add(sts);
                        }
                    }catch (Exception e){
                        logger.error("Error while delete data || " + consumerData.getConsumerName());
                    }
                } else {
                    StatusList sts = new StatusList();
                    sts.setResponses_code(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getCode());
                    sts.setResponse_message(ErrorCodeEnum.CONSUMERDATA_NOT_EXIST.getDefaultMsg());
                    sts.setConsumerName(consumerData.getConsumerName());
                    sts.setConsumerTopic(consumerData.getConsumerTopic());
                    stsList.add(sts);
                }
            }catch (Exception e){
                logger.error("Error while find data || " + consumerData.getConsumerName());
            }
        }
        s.setResponse_list(stsList);
        s.setResponse_timestamp(sdf.format(new Date()));

        return ResponseEntity.ok()
                .headers(CustomHeader.setHeaders())
                .body(s);
    }

}
