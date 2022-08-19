package com.bankmandiri.streamfailedpe.services;

import com.bankmandiri.streamfailedpe.model.ConsumerData;

import java.util.List;
import java.util.function.Consumer;

public interface ElasticService {

	public String insert(ConsumerData data);

	public ConsumerData getById(String data);

	public String deleteById(ConsumerData data);

	public List<ConsumerData> getListConsumerData();

}
