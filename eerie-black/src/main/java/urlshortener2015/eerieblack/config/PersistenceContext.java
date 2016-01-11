package urlshortener2015.eerieblack.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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
public class PersistenceContext {

	@Autowired
	protected JdbcTemplate jdbc;

	@Bean
	@Profile("shortener")
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}

	@Bean
	@Profile("shortener")
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}

	@Bean
	@Profile("users")
	UserRepository userRepository() {
		return new UserRepositoryImpl(jdbc);
	}
}
