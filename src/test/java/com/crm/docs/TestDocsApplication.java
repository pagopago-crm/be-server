package com.crm.docs;

import org.springframework.boot.SpringApplication;

public class TestDocsApplication {

	public static void main(String[] args) {
		SpringApplication.from(DocsApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
