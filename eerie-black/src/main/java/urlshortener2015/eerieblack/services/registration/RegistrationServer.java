package urlshortener2015.eerieblack.services.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class RegistrationServer {

    // Main is never used
    public static void main(String[] args) {
        // Tell server to look for application-registration.yml
        // System.setProperty("spring.config.name", "registration-server");
        SpringApplication.run(RegistrationServer.class, args);
    }
}

