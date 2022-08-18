package com.bankmandiri.streamfailedpe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerData {

    private String consumerName;
    private String consumerTopic;

}

