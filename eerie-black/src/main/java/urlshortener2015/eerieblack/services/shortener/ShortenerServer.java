package urlshortener2015.eerieblack.services.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShortenerServer extends SpringBootServletInitializer {

    // Load spring boot application on main
    // Main is never called
    public static void main(String[] args) throws Exception {
        System.setProperty("spring.profiles.active", "registration"); // Set profile to 'registration'
        SpringApplication.run(ShortenerServer.class, args);
    }

    // Override configuration to include this class as a source
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ShortenerServer.class);
    }
}
