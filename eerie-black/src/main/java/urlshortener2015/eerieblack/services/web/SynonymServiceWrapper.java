package urlshortener2015.eerieblack.services.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jorge on 14/01/2016.
 */
@Profile("web")
@Service
public class SynonymServiceWrapper{


    protected Logger logger = LoggerFactory.getLogger(SynonymServiceWrapper.class.getName());

    //protected String bigHugeLabsUrl = "http://words.bighugelabs.com/api/2/dc530c7236c1efc8bba96c2c5ba390f9";

    protected RestTemplate restTemplate;

    protected String serviceUrl;

    public SynonymServiceWrapper() {
        serviceUrl = "http://words.bighugelabs.com/api/2/dc530c7236c1efc8bba96c2c5ba390f9";
        restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    public String getSynonyms(String keyword){
        logger.info("Inicio metodo getSynonyms");
        logger.info("URL: "+  serviceUrl);
        logger.info("keyWord: " + keyword);
        String listSynonyms = restTemplate.getForObject(serviceUrl + "/{keyword}/json", String.class, keyword);

        logger.info("Fin metodo getSynonyms");
        return listSynonyms;
    }

}
