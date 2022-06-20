package com.bankmandiri.PE.dynamickafkaconsumer.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Request {

    private String topicName;
    private String consumerId;
    private Boolean consumerActivation = true;

}
