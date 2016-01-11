package urlshortener2015.eerieblack.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import urlshortener2015.eerieblack.domain.User;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class.getName());

    @Autowired
    protected JdbcTemplate jdbc;
    protected MessageDigest md;

    private static final RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString("username"), null, rs.getBoolean("premium"));
            // return new User(rs.getString("username"), rs.getString("password"), rs.getBoolean("premium"));
        }
    };

    public UserRepositoryImpl(){
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public UserRepositoryImpl(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    @Override
    public User save(User user) {
        try {
            jdbc.update("INSERT INTO users VALUES (?,?,?)",
                    user.getUsername(), encrypt(user.getPassword()), user.isPremium());
            user = new User(user.getUsername(), null, user.isPremium());
        } catch (DuplicateKeyException e) {
            log.debug("When insert for key " + user.getUsername(), e);
            return null;
        } catch (Exception e) {
            log.debug("When insert", e);
            return null;
        }
        return user;
    }

    @Override
    public User update(User user) {
        try {
            jdbc.update("update users set password=?, premium=? where username=?",
                    user.getUsername(), encrypt(user.getPassword()), user.isPremium());
            user = new User(user.getUsername(), null, user.isPremium());
		} catch (Exception e) {
			log.debug("When update for user " + user.getUsername(), e);
            return null;
		}
        return user;
    }

    @Override
    public User delete(User user) {
        try {
            jdbc.update("delete from users where hash=?", user.getUsername());
        } catch (Exception e) {
            log.debug("When delete for user " + user.getUsername(), e);
            return null;
        }
        return user;
    }

    @Override
    public List<User> list(Long limit, Long offset) {
        try {
            if (offset == null) offset = 0L;
            if (limit == null) limit = 100000000L;
            return jdbc.query("SELECT * FROM users LIMIT ? OFFSET ?",
                    new Object[] { limit, offset }, rowMapper);
        } catch (Exception e) {
            log.debug("When select for limit " + limit + " and offset "
                    + offset, e);
            return null;
        }
    }

    @Override
    public User validate(User user) {
        try {
            return jdbc.queryForObject("SELECT * FROM users WHERE username=? AND password=?",
                    rowMapper, user.getUsername(), encrypt(user.getPassword()));
        } catch (Exception e) {
            log.debug("When select for user " + user.getUsername(), e);
            return null;
        }
    }

    @Override
    public boolean isPremium(String name) {
        try {
            User user = jdbc.queryForObject("SELECT * FROM users WHERE username=?",
                    rowMapper, name);
            return user.isPremium();
        } catch (Exception e) {
            log.debug("When select for user " + name, e);
            return false;
        }
    }

    /* ENCRYPT HELPER */
    private byte[] encrypt(String original) throws UnsupportedEncodingException {
        byte[] messageBytes = original.getBytes("UTF-8");
        byte[] theDigest = md.digest(messageBytes);
        return theDigest;
    }
}
