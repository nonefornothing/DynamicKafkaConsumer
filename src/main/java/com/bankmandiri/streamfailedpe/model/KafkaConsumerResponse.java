package com.bankmandiri.streamfailedpe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KafkaConsumerResponse {

    private String consumerId;
    private String groupId;

    private String status;

    private List<KafkaConsumerAssignmentResponse> assignments;
}
