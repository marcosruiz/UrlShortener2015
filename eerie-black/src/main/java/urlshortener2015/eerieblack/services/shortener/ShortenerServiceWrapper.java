package urlshortener2015.eerieblack.services.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.eerieblack.auth.BearerTokenManager;

/** This class acts as a wrapper for the microservice URL-shortener. It will be used by other microservices */
@Profile("web") //Currently, the only service that will use this is 'web'
@Service
public class ShortenerServiceWrapper {

    @Autowired
    // @LoadBalanced can be added to balance loads between multiple instances of the service.
    // Currently this service isn't configured for replication, so using it may lead to data inconsistency.
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    protected Logger logger = LoggerFactory.getLogger(ShortenerServiceWrapper.class.getName());

    public ShortenerServiceWrapper() {
        serviceUrl = "http://shortener-service";
    }

    public ShortURL getByHash(String hash) {
        logger.info("Sending request to " + serviceUrl);
        ShortURL shortURL = restTemplate.getForObject(serviceUrl + "/{hash}", ShortURL.class, hash);
        logger.info("Received response from " + serviceUrl + ": " + shortURL);
        return shortURL;
    }

    public ShortURL postNewURL(String url, String sponsor, String brand, String token) {
        // Add parameters to request
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        if (url != null) data.add("url", url);
        if (sponsor != null) data.add("sponsor", sponsor);
        if (brand != null) data.add("brand", brand);
        HttpHeaders h = new HttpHeaders();
        if (token != null) BearerTokenManager.setAuthToken(token, h);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(data, h);
        // Send request
        logger.info("Sending request to " + serviceUrl + ": url=" + url + ", sponsor=" + sponsor + ", brand=" + brand);
        ShortURL shortURL = restTemplate.postForObject(serviceUrl + "/link", entity, ShortURL.class);
        logger.info("Received response from " + serviceUrl + ": " + url);
        return shortURL;
    }
}
