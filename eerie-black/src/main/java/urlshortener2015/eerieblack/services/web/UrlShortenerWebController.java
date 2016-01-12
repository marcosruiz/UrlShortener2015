package urlshortener2015.eerieblack.services.web;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.web.UrlShortenerController;
import urlshortener2015.eerieblack.services.shortener.ShortenerServiceWrapper;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Profile("web")
@ComponentScan(basePackages = { "urlshortener2015.common.repository", "urlshortener2015.eerieblack.services.shortener" }) // Añadir la ruta de todos los wrappers
public class UrlShortenerWebController extends UrlShortenerController {

    // JSON Web Token signing
    private static String KEY_SECRET = "super-secure-s3cr3t"; // FIXME OMG
    private static SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private static byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(KEY_SECRET);
    private static Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    @Autowired
    ShortenerServiceWrapper shortenerServiceWrapper;

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerWebController.class);

    @Override
	@RequestMapping(value = "/{id:(?!link|index|ad-redirect).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
        // Get url from shortenerService instead of own database
        ShortURL shortURL = shortenerServiceWrapper.getByHash(id);
        if (shortURL != null) {
            // If url has advertisement, change the target uri to our advertisement page
            if (true /* url has advertisement */) shortURL = interceptURIAndTarget(shortURL); //FIXME OBVIOUSLY
            return createSuccessfulRedirectToResponse(shortURL);
        }
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand, HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		ShortURL shortURL = shortenerServiceWrapper.postNewURL(url, sponsor, brand);
		if (shortURL != null) {
            shortURL = interceptURI(shortURL);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(shortURL.getUri());
			return new ResponseEntity<>(shortURL, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/{id:(?!link|index|ad-redirect).*}/key", method = RequestMethod.GET)
	public ResponseEntity<?> generateKey(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested key from " + extractIP(request));
        String key = generateRealTargetKey(extractIP(request), id);
        HttpHeaders h = new HttpHeaders();
        String result = "{\"key\":\"" + key + "\"}";  // F*CK DA POLICE
        h.setContentType(MediaType.APPLICATION_JSON); // THUG LIFE
		return new ResponseEntity<>(result, h, HttpStatus.OK);
	}

	@RequestMapping(value = "/{id:(?!link|index|ad-redirect).*}/realTarget", method = RequestMethod.GET)
	public ResponseEntity<?> getRealUri(@PathVariable String id,
                                        @RequestParam(value = "key", required = false) String key,
                                        HttpServletRequest request) {
		logger.info("Requested real uri from " + extractIP(request) + " for " + id);
		ShortURL shortURL = shortenerServiceWrapper.getByHash(id);
        if (shortURL != null) {
            if (key != null && validateRealTargetKey(key, extractIP(request), id)) {
                shortURL = interceptURI(shortURL);
                HttpHeaders h = new HttpHeaders();
                h.setLocation(shortURL.getUri());
                return new ResponseEntity<>(shortURL, h, HttpStatus.CREATED);
            } else return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        } else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}


    /* HELPER METHODS */

    // Modify shortURL URI to point to this web server instead of the shortener microservice
    private ShortURL interceptURI(ShortURL orig) {
        return new ShortURL(
                orig.getHash(),
                orig.getTarget(),
                linkTo(
                        methodOn(UrlShortenerWebController.class)
                                .redirectTo(orig.getHash(), null)
                ).toUri(),
                orig.getSponsor(),
                orig.getCreated(),
                orig.getOwner(),
                orig.getMode(),
                orig.getSafe(),
                orig.getIP(),
                orig.getCountry()
        );
    }

    // Modify shortURL URI to point to this web server instead of the shortener
    // microservice and target to point to our ad-page instead of the real one
    private ShortURL interceptURIAndTarget(ShortURL orig) {
        return new ShortURL(
                orig.getHash(),
                "/ad-redirect.html?id=" + orig.getHash(),
                linkTo(
                        methodOn(UrlShortenerWebController.class)
                                .redirectTo(orig.getHash(), null)
                ).toUri(),
                orig.getSponsor(),
                orig.getCreated(),
                orig.getOwner(),
                orig.getMode(),
                orig.getSafe(),
                orig.getIP(),
                orig.getCountry()
        );
    }

    // Generate JWT to access the {hash}/realTarget endpoint that has a retarded activation
    private String generateRealTargetKey(String ip, String hash) {

        //Establish dates
        long time = System.currentTimeMillis();
        Date now = new Date(time);
        Date activeAt = new Date(time + 5000); //Not active until 5 seconds later
        Date expiresAt = new Date(time + 60000); //Not active after 60 seconds

        // Set JWT claims
        JwtBuilder builder = Jwts.builder()
                .setAudience(ip)
                .setSubject(hash)
                .setIssuedAt(now)
                .setNotBefore(activeAt)
                .setExpiration(expiresAt)
                .signWith(signatureAlgorithm, signingKey);

        //Build JWT and serialize it to a compact, URL-safe string
        return builder.compact();
    }

    // Validate JWT
    private boolean validateRealTargetKey(String key, String ip, String hash) {
        try {
            Claims claims = Jwts.parser().setSigningKey(apiKeySecretBytes).parseClaimsJws(key).getBody();
            return claims.getAudience().equals(ip) && claims.getSubject().equals(hash);
        } catch (SignatureException e) {
            logger.info("Trying to use an unsigned key from " + ip);
            return false;
        } catch (PrematureJwtException e) {
            logger.info("Trying to use a key before its activation from " + ip);
            return false;
        } catch (ExpiredJwtException e) {
            logger.info("Trying to use an expired key from " + ip);
            return false;
        }
    }

}
