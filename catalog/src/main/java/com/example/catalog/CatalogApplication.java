package com.example.catalog;

import lombok.extern.slf4j.Slf4j;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Map;
import java.util.Set;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@SpringBootApplication
public class CatalogApplication {

	public static void main(String[] args) {
		log.info("Starting Catalog Service");
		SpringApplication.run(CatalogApplication.class, args);
	}
}


@RestController
class AvailabilityController {

		private boolean validate(String console) {
				return StringUtils.hasText(console) &&
						Set.of("ps5", "ps4", "switch", "xbox").contains(console);
		}

		@GetMapping("/availability/{console}")
		Map<String, Object> getAvailability(@PathVariable String console) {
				return Map.of("console", console,
								"available", checkAvailability(console));
		}

		private boolean checkAvailability(String console) {
				Assert.state(validate(console), () -> "the console specified, " + console + ", is not valid.");
				if("ps5".equals(console)){
					throw new RuntimeException("Service exception");
				}else if( "xbox".equals(console)){
					return true;
				}else{
					return false;
				}
		}
}
