package com.bankmandiri.PE.dynamickafkaconsumer.adapter.api.impl;

import com.bankmandiri.PE.dynamickafkaconsumer.adapter.BaseClientAdapter;
import com.bankmandiri.PE.dynamickafkaconsumer.adapter.api.ClientAdapter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.net.ConnectException;


@Service
public class ClientAdapterImpl extends BaseClientAdapter implements ClientAdapter {

	/**
    *
    * CLient adapter for REST client
    * 
    */
	
	@Override
	public String paramRequest(String body,String uri) throws ConnectException  {
		HttpMethod method = HttpMethod.POST;
		MediaType mediaType = MediaType.APPLICATION_JSON;
		return sendRequest(uri, body, method, mediaType);
	}
	
}
