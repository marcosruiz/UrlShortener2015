package urlshortener2015.eerieblack.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.ObjectToStringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import javax.ws.rs.GET;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by jorge on 14/01/2016.
 */
@Profile("web")
//@Service
@Controller
public class SynonymServiceWrapper {


    protected Logger logger = LoggerFactory.getLogger(SynonymServiceWrapper.class.getName());

    //protected String bigHugeLabsUrl = "http://words.bighugelabs.com/api/2/dc530c7236c1efc8bba96c2c5ba390f9";

    @Autowired
    // @LoadBalanced can be added to balance loads between multiple instances of the service.
    // Currently this service isn't configured for replication, so using it may lead to data inconsistency.
    protected RestTemplate restTemplate;

    @SuppressWarnings("unchecked")
    //public List<String> getSynonyms(String keyWord){
    public Object getSynonyms(String keyword){
        logger.info("Inicio metodo getSynonyms");
       // List<String>listSynonyms = restTemplate.getForObject(bigHugeLabsUrl + "/{keyWord}/json",List.class, keyWord);
        //String listSynonyms = restTemplate.getForObject(bigHugeLabsUrl + "/{keyWord}/",String.class, keyWord);
        //String listSynonyms ="asasbnjasjasbjashjaghasghasghasghghasghasghasghasghasghas";
        String bigHugeLabsUrl = "http://words.bighugelabs.com/api/2/dc530c7236c1efc8bba96c2c5ba390f9";
        logger.info("URL: "+  bigHugeLabsUrl);
        logger.info("keyWord: "+ keyword);


        Object listSynonyms = restTemplate.getForObject(bigHugeLabsUrl + "/{keyword}/",Object.class, keyword);
        //Object listSynonyms = restTemplate.execute(bigHugeLabsUrl+"/Chair/", HttpMethod.GET,null,null,new Object());
       /* int myResponseCode = 0;
        Object listSynonyms=null;
        try {

            URL urlTest = new URL(bigHugeLabsUrl+"/chair/");
            HttpURLConnection http = (HttpURLConnection) urlTest.openConnection();
            myResponseCode = http.getResponseCode();

            listSynonyms=http.;

            logger.info(""+myResponseCode);
        }catch (Exception e){
            logger.info(            ""+myResponseCode);
        }
*/
        logger.info("sdsdsds");
        return listSynonyms;
    }

}
