/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bankmandiri.streamfailedpe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public class Status {
    
    private String engine_type = "BWXKAFKA";
    private String error_code;
    private List<StatusList> response_list;
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
	public List<StatusList> getResponse_list() {
		return response_list;
	}
	public void setResponse_list(List<StatusList> response_list) {
		this.response_list = response_list;
	}
	public String getResponse_timestamp() {
		return response_timestamp;
	}
	public void setResponse_timestamp(String response_timestamp) {
		this.response_timestamp = response_timestamp;
	}
	

	

    
    
    
}
