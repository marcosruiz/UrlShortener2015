package urlshortener2015.eerieblack.services.shortener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import urlshortener2015.common.domain.ShortURL;

/** This class acts as a wrapper for the microservice URL-shortener */
@Service
public class ShortenerServiceWrapper {

    @Autowired
    @LoadBalanced
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    protected Logger logger = LoggerFactory.getLogger(ShortenerServiceWrapper.class.getName());

    public ShortenerServiceWrapper() {
        serviceUrl = "http://shortener-service";
    }
    public ShortenerServiceWrapper(String serviceUrl) {
        this.serviceUrl = serviceUrl.startsWith("http") ?
                serviceUrl : "https://" + serviceUrl;
    }

    public ShortURL getByHash(String hash) {
        ShortURL url = restTemplate.getForObject(serviceUrl + "/{hash}", ShortURL.class, hash);
        return url;
    }

    // public void postNewURL(String hash) {
    //     ShortURL url = restTemplate.getForObject(serviceUrl + "/link", ShortURL.class, hash);
    //     return url;
    // }
}
