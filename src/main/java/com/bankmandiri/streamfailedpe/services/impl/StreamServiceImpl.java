package com.bankmandiri.streamfailedpe.services.impl;

import com.bankmandiri.streamfailedpe.adapter.api.ClientAdapter;
import com.bankmandiri.streamfailedpe.services.StreamService;
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
