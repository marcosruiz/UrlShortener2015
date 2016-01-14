package urlshortener2015.eerieblack.services.shortener;

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

@Configuration
@Profile("shortener")
@ComponentScan(basePackages = { "urlshortener2015.common.repository", "urlshortener2015.eerieblack.services.shortener" })
public class PersistenceContext {

	@Autowired
	protected JdbcTemplate jdbc;

	@Bean
	ShortURLRepository shortURLRepository() {
		return new ShortURLRepositoryImpl(jdbc);
	}

	@Bean
	ClickRepository clickRepository() {
		return new ClickRepositoryImpl(jdbc);
	}

	@Bean
	ShortURLExtRepository shortURLExtRepository() {return new ShortURLExtRepositoryImpl(jdbc);}

}
