package urlshortener2015.eerieblack.services.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
// @ ComponentScan(useDefaultFilters = false)
public class WebServer extends SpringBootServletInitializer {

	// Set profile to "web" and load Spring Boot Application
	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "web");
		SpringApplication.run(WebServer.class, args);
	}

    // Override configuration to include this class as a source
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebServer.class);
	}
}
