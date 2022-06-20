package com.bankmandiri.PE.dynamickafkaconsumer.services;

import java.net.ConnectException;

public interface StreamService {
	
	public String sendData (String request,String uri) throws ConnectException;

}
