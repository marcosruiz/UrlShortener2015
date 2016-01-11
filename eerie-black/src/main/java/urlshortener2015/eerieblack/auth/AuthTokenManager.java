package urlshortener2015.eerieblack.auth;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import urlshortener2015.eerieblack.domain.User;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

public class AuthTokenManager {

    private static String KEY_SECRET = "sup3r-s3cr3t-API-k3y"; // FIXME OMG
    private static int    TTL_MILLIS = 3600000; // 1h TTL

    private static Logger logger = LoggerFactory.getLogger(AuthTokenManager.class.getName());

    // JSON Web Token signing
    private static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(KEY_SECRET);
    private static Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());


    /** Generate JWT to access a protected endpoint */
    public static String generateAuthToken(User user) {

        //Establish dates
        long time = System.currentTimeMillis();
        Date now = new Date(time);
        Date expiresAt = new Date(time + TTL_MILLIS); //Not active after 60 seconds

        // Set JWT claims
        JwtBuilder builder = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("premium", user.isPremium())
                .setIssuedAt(now)
                .setExpiration(expiresAt)
                .signWith(signatureAlgorithm, signingKey);

        //Build JWT and serialize it to a compact, URL-safe string
        return builder.compact();
    }

    /** Validates JWT to access a protected endpoint and returns user info */
    public static User validateAuthToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token).getBody();
            return new User(claims.get("username", String.class), null, claims.get("premium", Boolean.class));
        } catch (ExpiredJwtException e) {
            logger.info("Trying to use an expired key");
            return null;
        } catch (SignatureException | NullPointerException | MalformedJwtException e) {
            logger.info("Trying to use an invalid key");
            return null;
        }
    }

    /** Extracts the token from the provided request */
    public static String extractAuthToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.toLowerCase().startsWith("bearer")) return header.substring(7);
        else return null;
    }

    /** Injects the auth token inside the provided headers */
    public static HttpHeaders setAuthToken(String token, HttpHeaders headers) {
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return headers;
    }
}
