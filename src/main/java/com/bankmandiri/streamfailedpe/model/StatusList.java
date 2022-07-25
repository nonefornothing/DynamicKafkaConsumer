package com.bankmandiri.streamfailedpe.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class StatusList {
	
	private String account_no;
	private String responses_code;
	private Object response_message;
	
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
	public String getAccount_no() {
		return account_no;
	}
	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}
	  
	  
	  

}
