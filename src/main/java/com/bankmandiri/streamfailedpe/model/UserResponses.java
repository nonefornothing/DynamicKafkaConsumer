/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankmandiri.streamfailedpe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class UserResponses {
    
    private String engine_type = "BWXKAFKA";
    private String error_code;
    private String responses_code;
    private Object response_message;
    private String response_timestamp;
	public String getEngine_type() {
		return engine_type;
	}
	public void setEngine_type(String engine_type) {
		this.engine_type = engine_type;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getResponses_code() {
		return responses_code;
	}
	public void setResponses_code(String responses_code) {
		this.responses_code = responses_code;
	}
	public Object getResponse_message() {
		return response_message;
	}
	public void setResponse_message(Object response_message) {
		this.response_message = response_message;
	}
	public String getResponse_timestamp() {
		return response_timestamp;
	}
	public void setResponse_timestamp(String response_timestamp) {
		this.response_timestamp = response_timestamp;
	}

    
    
    
}
