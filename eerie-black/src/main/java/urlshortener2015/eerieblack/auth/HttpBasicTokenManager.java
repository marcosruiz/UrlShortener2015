package urlshortener2015.eerieblack.auth;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.codec.Base64;
import urlshortener2015.eerieblack.domain.User;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.Key;

public class HttpBasicTokenManager {

    private static String KEY_SECRET = "sup3r-s3cr3t-API-k3y"; // FIXME OMG
    private static int    TTL_MILLIS = 3600000; // 1h TTL

    private static Logger logger = LoggerFactory.getLogger(HttpBasicTokenManager.class.getName());

    // JSON Web Token signing
    private static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(KEY_SECRET);
    private static Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    /** Validates JWT to access a protected endpoint and returns user info */
    public static User validateToken(String token) {
        try {
            if (token == null) return null;
            byte[] decodedBytes = Base64.decode(token.getBytes(Charset.forName("UTF-8")));
            String[] info = new String(decodedBytes).split(":");
            return new User(info[0], info[1], false);
        } catch (NullPointerException e) {
            logger.info("Trying to use an invalid key");
            return null;
        }
    }

    /** Extracts the token from the provided request */
    public static String extractToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.toLowerCase().startsWith("basic")) return header.substring(6);
        else return null;
    }

    /** Injects the auth token inside the provided headers */
    public static HttpHeaders setToken(String token, HttpHeaders headers) {
        headers.set(HttpHeaders.AUTHORIZATION, "Basic " + token);
        return headers;
    }
}
