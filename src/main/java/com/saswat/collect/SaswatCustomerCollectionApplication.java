package com.saswat.collect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("file:config/application.properties")
@ComponentScan(basePackages= {"com.saswat"})
public class SaswatCustomerCollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(SaswatCustomerCollectionApplication.class, args);
	}

}
