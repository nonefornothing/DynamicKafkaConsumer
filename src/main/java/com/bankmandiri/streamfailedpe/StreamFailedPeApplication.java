package com.bankmandiri.streamfailedpe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;

@EnableMBeanExport(registration = RegistrationPolicy.REPLACE_EXISTING)
@SpringBootApplication
public class StreamFailedPeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StreamFailedPeApplication.class, args);
	}

}
