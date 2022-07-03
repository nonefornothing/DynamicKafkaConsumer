package com.bankmandiri.streamfailedpe.services.impl;

import com.bankmandiri.streamfailedpe.adapter.api.ClientAdapter;
import com.bankmandiri.streamfailedpe.services.StreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StreamServiceImpl implements StreamService {
	
	@Autowired
	private ClientAdapter clientAdapter;
	
	@Override
	public ResponseEntity<String> sendData(String request,String uri) throws Exception {
		return clientAdapter.paramRequest( request,uri);
	}

}
