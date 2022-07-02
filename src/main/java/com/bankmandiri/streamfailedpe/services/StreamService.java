package com.bankmandiri.streamfailedpe.services;

import java.net.ConnectException;

public interface StreamService {
	
	public String sendData (String request,String uri) throws ConnectException;

}
