package com.bankmandiri.PE.dynamickafkaconsumer.adapter.api;

import java.net.ConnectException;

public interface ClientAdapter {

	String paramRequest(String body, String uri) throws ConnectException;

}
