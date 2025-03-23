package com.uddco;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class UddCoApplication {

	public static void main(String[] args) {
		SpringApplication.run(UddCoApplication.class, args);
	}

}
