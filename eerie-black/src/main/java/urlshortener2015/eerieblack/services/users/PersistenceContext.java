package urlshortener2015.eerieblack.services.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import urlshortener2015.common.repository.ClickRepository;
import urlshortener2015.common.repository.ClickRepositoryImpl;
import urlshortener2015.common.repository.ShortURLRepository;
import urlshortener2015.common.repository.ShortURLRepositoryImpl;
import urlshortener2015.eerieblack.repository.UserRepository;
import urlshortener2015.eerieblack.repository.UserRepositoryImpl;

@Configuration
@Profile("users")
@ComponentScan(basePackages = { "urlshortener2015.common.repository" })
public class PersistenceContext {

	@Autowired
	protected JdbcTemplate jdbc;

	@Bean
	UserRepository userRepository() {
		return new UserRepositoryImpl(jdbc);
	}
}
