package urlshortener2015.eerieblack.services.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.repository.ShortURLRepositoryImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Marcos on 13/01/2016.
 */
public class ShortURLExtRepositoryImpl extends ShortURLRepositoryImpl implements ShortURLExtRepository{

    public ShortURLExtRepositoryImpl() {
    }

    public ShortURLExtRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final Logger log = LoggerFactory
            .getLogger(ShortURLExtRepositoryImpl.class);

    private static final RowMapper<ShortURL> rowMapper = new RowMapper<ShortURL>() {
        @Override
        public ShortURL mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new ShortURL(rs.getString("hash"), rs.getString("target"),
                    null, rs.getString("sponsor"), rs.getDate("created"),
                    rs.getString("owner"), rs.getInt("mode"),
                    rs.getBoolean("safe"), rs.getString("ip"),
                    rs.getString("country"));
        }
    };

    @Override
    public List<ShortURL> list() {
        try {
            return jdbc.query("SELECT * FROM shorturl", rowMapper);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
