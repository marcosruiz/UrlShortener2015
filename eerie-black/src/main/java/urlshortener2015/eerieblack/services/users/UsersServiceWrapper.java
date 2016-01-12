package urlshortener2015.eerieblack.services.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import urlshortener2015.eerieblack.auth.AuthTokenManager;
import urlshortener2015.eerieblack.domain.User;

import java.util.List;

/** This class acts as a wrapper for the users microservice. It will be used by other microservices */
@Profile("web") //Currently, the only service that will use this is 'web'
@Service
public class UsersServiceWrapper {

    @Autowired
    // @LoadBalanced can be added to balance loads between multiple instances of the service.
    // Currently this service isn't configured for replication, so using it may lead to data inconsistency.
    protected RestTemplate restTemplate;

    protected String serviceUrl;

    protected Logger logger = LoggerFactory.getLogger(UsersServiceWrapper.class.getName());

    public UsersServiceWrapper() {
        serviceUrl = "http://users-service";
    }

    // GET users list
    @SuppressWarnings("unchecked")
    public List<User> getUsersList(Long limit, Long offset) {
        logger.info("Sending request to " + serviceUrl);
        List<User> list =
            restTemplate.getForObject(serviceUrl + "/users?limit={limit}&offset={offset}",
                    List.class, limit, offset);
        logger.info("Received response from " + serviceUrl);
        return list;
    }

    // POST new user
    public User postNewUser(String username, String password, Boolean premium) {
        // Add parameters to request
        MultiValueMap<String, Object> data = new LinkedMultiValueMap<>();
        data.add("username", username);
        data.add("password", password);
        if (premium != null) data.add("premium", premium);
        // Send request
        logger.info("Sending request to " + serviceUrl);
        User user = restTemplate.postForObject(serviceUrl + "/users", data, User.class);
        logger.info("Received response from " + serviceUrl);
        return user;
    }

    // Generate auth token
    public String generateAuthToken(String username, String password) {
        // Add parameters to request
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("username", username);
        data.add("password", password);
        // Send request
        logger.info("Sending request to " + serviceUrl);
        String token = restTemplate.postForObject(serviceUrl + "/auth", data, String.class);
        logger.info("Received response from " + serviceUrl);
        return token;
    }

    // Update user's password
    public User updateUserPass(String name, String password, String authToken) {
        // NOTE: RestTemplate.put() is void, so it doesn't provide any information about
        // the response. We will use RestTemplate.exchange() instead.
        logger.info("Sending request to " + serviceUrl);
        HttpHeaders h = new HttpHeaders();
        AuthTokenManager.setAuthToken(authToken, h);
        HttpEntity<String> entity = new HttpEntity<>(password, h);
        ResponseEntity<User> response =
                restTemplate.exchange(serviceUrl + "/users/{name}/password", HttpMethod.PUT, entity, User.class, name);
        logger.info("Received response from " + serviceUrl);
        return response.getBody();
    }

    // Update user's premium status
    public User updateUserPremium(String name, Boolean premium, String authToken) {
        // NOTE: RestTemplate.put() is void, so it doesn't provide any information about
        // the response. We will use RestTemplate.exchange() instead.
        logger.info("Sending request to " + serviceUrl);
        HttpHeaders h = new HttpHeaders();
        AuthTokenManager.setAuthToken(authToken, h);
        HttpEntity<String> entity = new HttpEntity<>(premium.toString(), h);
        ResponseEntity<User> response =
                restTemplate.exchange(serviceUrl + "/users/{name}/premium", HttpMethod.PUT, entity, User.class, name);
        logger.info("Received response from " + serviceUrl);
        return response.getBody();
    }

    // DELETE user
    public User deleteUser(String name, String authToken) {
        // NOTE: RestTemplate.delete() is void, so it doesn't provide any information about
        // the response. We will use RestTemplate.exchange() instead.
        logger.info("Sending request to " + serviceUrl);
        HttpHeaders h = new HttpHeaders();
        AuthTokenManager.setAuthToken(authToken, h);
        HttpEntity<String> entity = new HttpEntity<>(h);
        ResponseEntity<User> response =
                restTemplate.exchange(serviceUrl + "/users/{name}", HttpMethod.DELETE, entity, User.class, name);
        logger.info("Received response from " + serviceUrl);
        return response.getBody();
    }
}
