package urlshortener2015.eerieblack

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

@SpringBootApplication
class ApplicationScala extends SpringBootServletInitializer {
  override def configure(application:SpringApplicationBuilder): SpringApplicationBuilder = {
    application.sources(classOf[ApplicationScala])
  }
}

// Scala must have main method defined on an object
object Main {
  def main(args: Array[String]): Unit = {
    SpringApplication.run(classOf[ApplicationScala])
  }
}
