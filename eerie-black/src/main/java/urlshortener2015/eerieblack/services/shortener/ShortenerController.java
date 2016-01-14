package urlshortener2015.eerieblack.services.shortener;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.web.UrlShortenerController;
import urlshortener2015.eerieblack.auth.AuthTokenManager;
import urlshortener2015.eerieblack.domain.User;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Profile("shortener")
@ComponentScan(basePackages = { "urlshortener2015.common.repository" })
public class ShortenerController extends UrlShortenerController {

    private static final Logger logger = LoggerFactory.getLogger(ShortenerController.class);

    @Override
    @RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
    public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
        logger.info("Requested redirection with hash " + id);
        return super.redirectTo(id, request);
    }

    @Override
    public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
                                              @RequestParam(value = "sponsor", required = false) String sponsor,
                                              @RequestParam(value = "brand", required = false) String brand,
                                              HttpServletRequest request) {
        logger.info("Requested new short for uri " + url);

        // Get the token from request
        String token = AuthTokenManager.extractAuthToken(request);
        if (token == null && sponsor != null && sponsor.equals("no")) {
            logger.info("Auth fail: No token found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Validate the token
        User user = AuthTokenManager.validateAuthToken(token);
        if ((user == null || !user.isPremium()) && sponsor != null && sponsor.equals("no")) {
            logger.info("Auth fail: " + (user == null ? "invalid token" : "user not authorized"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        //We test if the URI is reachable
        int myResponseCode = 0;
        try {
            URL urlTest = new URL(url);
            HttpURLConnection http = (HttpURLConnection)urlTest.openConnection();
            myResponseCode = http.getResponseCode();
            if(200 <= myResponseCode && 300 > myResponseCode ){
                logger.info("The response code for the uri " + url + " is " + myResponseCode + " REACHABLE");
                return super.shortener(url, sponsor, brand, request);
            }else{
                logger.info("The response code for the uri " + url + " is " + myResponseCode + " NOT REACHABLE");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            logger.info("The response code for the uri " + url + " is " + myResponseCode + " NOT REACHABLE");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }





    }

    @Override
    protected ShortURL createAndSaveIfValid(String url, String sponsor,
                                            String brand, String owner, String ip) {
        if (sponsor == null || sponsor.equals("")) sponsor = "yes";
        UrlValidator urlValidator = new UrlValidator(new String[] { "http", "https" });
        if (urlValidator.isValid(url)) {
            if(brand == null || brand.equals("")){
                brand = Hashing.murmur3_32()
                        .hashString(url, StandardCharsets.UTF_8).toString();
            }
            ShortURL s = shortURLRepository.findByKey(brand);
            // Si existe con antelacion y las uris no coinciden, devuelve null
            if(s != null && s.getTarget() != null && !s.getTarget().equals(url)){
                return null;
            }
            ShortURL su = new ShortURL(brand, url,
                    linkTo(methodOn(UrlShortenerController.class).redirectTo(brand, null)).toUri(),
                    sponsor, new Date(System.currentTimeMillis()), owner,
                    HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
            return shortURLRepository.save(su);
        } else {
            return null;
        }
    }

    // Modify redirection creation to return stored data instead. The service which consumes this API will
    // redirect the data to the target URI or to an advertisement intermediate page
    @Override
    protected ResponseEntity<?> createSuccessfulRedirectToResponse(ShortURL l) {
        HttpHeaders h = new HttpHeaders();
        return new ResponseEntity<>(l, h, HttpStatus.OK);
    }
}
