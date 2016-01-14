package urlshortener2015.eerieblack.services.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.repository.ShortURLRepositoryImpl;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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

    @Override
    public boolean isNotReachable(ShortURL su){
        try {
            Date now = Date.valueOf(LocalDate.now());
            jdbc.update("INSERT INTO not_reachables VALUES (?,?)", su.getHash(), now);
        } catch (DuplicateKeyException e) {
            log.debug("When insert for key " + su.getHash(), e);
            return false;
        } catch (Exception e) {
            log.debug("When insert", e);
            return false;
        }
        Integer mode = new Integer(50);
        ShortURL suAux = new ShortURL(su.getHash(),su.getTarget(),
                su.getUri(),su.getSponsor(),su.getCreated(),su.getOwner(),
                mode, su.getSafe(),su.getIP(),su.getCountry());
        update(suAux);
        return true;
    }

    @Override
    public boolean isReachable(ShortURL su){
        try {
            jdbc.update("delete from not_reachables where hash=?", su.getHash());
        } catch (Exception e) {
            log.debug("When delete for hash " + su.getHash(), e);
            return false;
        }
        Integer mode = new Integer(307);
        ShortURL suAux = new ShortURL(su.getHash(),su.getTarget(),
                su.getUri(),su.getSponsor(),su.getCreated(),su.getOwner(),
                mode, su.getSafe(),su.getIP(),su.getCountry());
        update(suAux);
        return true;
    }

    @Override
    public Date notReachableSince(String id){
        try {
            return jdbc.queryForObject("SELECT not_reachable_since FROM not_reachables WHERE hash=?", Date.class, id);
        } catch (Exception e) {
            log.debug("When select for key " + id, e);
            return null;
        }
    }
}
