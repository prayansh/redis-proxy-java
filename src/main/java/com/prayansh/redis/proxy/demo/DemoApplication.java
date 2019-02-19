package com.prayansh.redis.proxy.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {
	@Value("${proxy.config.redis.hostname}")
	private String hostName;
	@Value("${proxy.config.redis.port}")
	private int portNumber;
	@Value("${proxy.config.cachesize}")
	private long timeToExpireMillis;
	@Value("${proxy.config.cachettl}")
	private int cacheCapacity;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
