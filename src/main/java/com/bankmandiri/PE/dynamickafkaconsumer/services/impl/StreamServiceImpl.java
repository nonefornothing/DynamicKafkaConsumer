package com.bankmandiri.PE.dynamickafkaconsumer.services.impl;

import com.bankmandiri.PE.dynamickafkaconsumer.adapter.api.ClientAdapter;
import com.bankmandiri.PE.dynamickafkaconsumer.services.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.ConnectException;

@Service
public class StreamServiceImpl implements StreamService {
	
	@Autowired
	private ClientAdapter clientAdapter;
	
	@Override
	public String sendData(String request,String uri) throws ConnectException {
		return clientAdapter.paramRequest( request,uri);
	}

}
