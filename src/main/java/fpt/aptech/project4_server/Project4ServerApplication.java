package fpt.aptech.project4_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import fpt.aptech.project4_server.config.AppProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Project4ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Project4ServerApplication.class, args);
	}

}
