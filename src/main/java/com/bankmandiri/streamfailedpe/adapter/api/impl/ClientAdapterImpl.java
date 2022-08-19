package com.bankmandiri.streamfailedpe.adapter.api.impl;

import com.bankmandiri.streamfailedpe.adapter.BaseClientAdapter;
import com.bankmandiri.streamfailedpe.adapter.api.ClientAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ClientAdapterImpl extends BaseClientAdapter implements ClientAdapter {

	/**
    *
    * Client adapter for REST client
    * 
    */

	@Override
	public ResponseEntity<String> paramRequest(String body, String uri) throws Exception  {
		HttpMethod method = HttpMethod.POST;
		return sendRequest(uri, body, method);
	}
	
}
