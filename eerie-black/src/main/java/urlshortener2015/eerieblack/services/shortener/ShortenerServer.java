package urlshortener2015.eerieblack.services.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableDiscoveryClient
public class ShortenerServer extends SpringBootServletInitializer{

    //GUARRADA: this should not exist
    private static ApplicationContext ctx;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    private void init(){
        ShortenerServer.ctx = applicationContext;
    }

    // Set profile to "shortener" and load Spring Boot Application
    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "shortener");
        SpringApplication.run(ShortenerServer.class, args);

        //GUARRADA: Starts checkerThread
        CheckerThread ct = (CheckerThread) ctx.getBean("checkerThread");
        ct.start();

    }


}
