package urlshortener2015.eerieblack.services.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
public class WebServer extends SpringBootServletInitializer {

    // Load spring boot application on main
	public static void main(String[] args) throws Exception {
		SpringApplication.run(WebServer.class, args);
	}

    // Override configuration to include this class as a source
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(WebServer.class);
	}

}
