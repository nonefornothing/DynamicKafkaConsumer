package com.bankmandiri.streamfailedpe.adapter.api;

import org.springframework.http.ResponseEntity;

public interface ClientAdapter {

	ResponseEntity<String> paramRequest(String body, String uri) throws Exception;

}
