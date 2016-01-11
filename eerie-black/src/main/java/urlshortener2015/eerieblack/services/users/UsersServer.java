package urlshortener2015.eerieblack.services.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UsersServer extends SpringBootServletInitializer {

    // Set profile to "users" and load Spring Boot Application
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "users");
        SpringApplication.run(UsersServer.class, args);
    }
}

