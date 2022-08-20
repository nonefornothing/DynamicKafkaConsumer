package com.bankmandiri.streamfailedpe.services;

import org.springframework.http.ResponseEntity;

public interface StreamService {
	
	ResponseEntity<String> sendData (String request, String uri) throws Exception;

}
