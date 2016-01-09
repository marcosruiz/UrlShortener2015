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

    // Load spring boot application on main (never used)
	public static void main(String[] args) throws Exception {
		System.setProperty("spring.profiles.active", "web");
		SpringApplication.run(WebServer.class, args);
	}

    // Override configuration to include this class as a source
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebServer.class);
	}

	// @Bean
	// public ShortenerService shortenerService() {
	// 	return new ShortenerService("http://shortener-service");
	// }

	// @Bean
	// public UrlShortenerControllerWithLogs urlShortenerControllerWithLogs() {
	// 	return new UrlShortenerControllerWithLogs(shortenerService());
	// }

}
