package urlshortener2015.eerieblack.services.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RegistrationServer {

    // Set profile to "registration" and load Spring Boot Application
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "registration");
        SpringApplication.run(RegistrationServer.class, args);
    }
}

