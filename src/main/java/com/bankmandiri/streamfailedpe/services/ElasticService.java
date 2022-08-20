package com.bankmandiri.streamfailedpe.services;

import com.bankmandiri.streamfailedpe.model.ConsumerData;

import java.util.List;

public interface ElasticService {

	String insert(ConsumerData data);

	ConsumerData getById(String data);

	String deleteById(ConsumerData data);

	List<ConsumerData> getListConsumerData();

}
