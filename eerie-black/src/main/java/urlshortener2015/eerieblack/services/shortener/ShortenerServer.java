package urlshortener2015.eerieblack.services.shortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ShortenerServer extends SpringBootServletInitializer {

    // Set profile to "shortener" and load Spring Boot Application
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "shortener");
        SpringApplication.run(ShortenerServer.class, args);
    }
}
