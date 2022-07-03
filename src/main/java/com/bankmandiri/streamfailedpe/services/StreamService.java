package com.bankmandiri.streamfailedpe.services;

import org.springframework.http.ResponseEntity;

public interface StreamService {
	
	public ResponseEntity<String> sendData (String request, String uri) throws Exception;

}
