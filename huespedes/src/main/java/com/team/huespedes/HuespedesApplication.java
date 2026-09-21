package com.team.huespedes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.team.huespedes", "com.team.common"})
public class HuespedesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HuespedesApplication.class, args);
	}

}
