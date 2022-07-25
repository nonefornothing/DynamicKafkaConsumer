package com.bankmandiri.streamfailedpe.utils;

import org.springframework.http.HttpHeaders;

public class CustomHeader {
	
	public static HttpHeaders setHeaders() {
		HttpHeaders header = new HttpHeaders();
		header.add("Content-Security-Policy", "script-src 'self' js.example.com");
		header.add("Strict-Transport-Security", "max-age=16070400; includeSubDomains");
		header.add("X-Permitted-Cross-Domain-Policies", "none");
		header.add("Referrer-Policy", "no-referrer");
		header.add("Feature-Policy", "vibrate 'none'; geolocation 'none'");
		return header;
	}

}
