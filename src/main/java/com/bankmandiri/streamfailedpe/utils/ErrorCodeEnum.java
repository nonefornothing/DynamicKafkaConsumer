package com.bankmandiri.streamfailedpe.utils;

import java.util.HashMap;
import java.util.Map;

public enum ErrorCodeEnum {
	
	SUCCESS						("00","Process successful"),
	READ_DATA_EXIST				("00","Account Number Exist"),
	REQUEST_NOT_VALID			("01","Request not valid"),
	ACTION_MODE_NOT_VALID		("02","Action Mode Not Valid"),
	CREATE_ACCNUM_NULL		("03","Cannot Create Null Account Number"),
	ACCNUM_NOT_EXIST			("04","Account Number Not Exist"),
	DELETE_ACCNUM_NULL		("05","Cannot Delete Null or Empty Account Number"),
	READ_DATA_FAILED			("06","Read data failed"),
	ADD_DATA_FAILED				("07","Add data failed"),
	DELETE_DATA_FAILED			("08","Delete data failed"),
	DATA_EXIST					("09","Data Exist"),
	SPECIAL_CHAR  				("10","Special Character Not Allowed"),
	
	SIGNIN_FAILED				("11","Signin failed"),
	USERNAME_NOT_VALID			("12","Username Not Valid"),
	PASS_NOT_VALID				("13","Password Not Valid"),
	
	
	UNKNOWN_ERROR				("99","Unknown Error"),
	;
	
	private String code;
	private String defaultMsg;
	
	private static final Map<String, ErrorCodeEnum> lookup = new HashMap<String, ErrorCodeEnum>();
	
	static {
        for (ErrorCodeEnum d : ErrorCodeEnum.values()) {
            lookup.put(d.getCode(), d);
        }
    }
	
	private ErrorCodeEnum(String code, String defaultMsg) {
		this.code = code;
		this.defaultMsg = defaultMsg;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDefaultMsg() {
		return defaultMsg;
	}

	public void setDefaultMsg(String defaultMsg) {
		this.defaultMsg = defaultMsg;
	}
	
	

}
