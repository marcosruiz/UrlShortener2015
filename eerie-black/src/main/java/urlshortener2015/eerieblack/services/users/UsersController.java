package urlshortener2015.eerieblack.services.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.eerieblack.auth.BearerTokenManager;
import urlshortener2015.eerieblack.domain.User;
import urlshortener2015.eerieblack.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Profile("users")
@ComponentScan(basePackages = { "urlshortener2015.common.repository" })
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);

    @Autowired
    UserRepository userRepository;

    // GET users list
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestParam(value = "limit", required = false) Long limit,
                                     @RequestParam(value = "offset", required = false) Long offset,
                                     HttpServletRequest request) {
        logger.info("Requested users list starting on " + offset + " with limit " + limit);
        List<User> usersList = userRepository.list(limit, offset);
        if (usersList != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(usersList, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // POST new user
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public ResponseEntity<?> postUser(@RequestParam(value = "username", required = true) String name,
                                      @RequestParam(value = "password", required = true) String pass,
                                      @RequestParam(value = "premium", required = false) Boolean premium,
                                     HttpServletRequest request) {
        if (premium == null) premium = false;
        User user = new User(name, pass, premium);
        logger.info("Requested user creation for name " + user.getUsername());
        User newUser = userRepository.save(user);
        if (newUser != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(newUser, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Generate auth token
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> authUser(@RequestParam(value = "username", required = true) String name,
                                      @RequestParam(value = "password", required = true) String pass,
                                      HttpServletRequest request) {
        User user = userRepository.validate(new User(name, pass, false));
        if (user != null) {
            HttpHeaders h = new HttpHeaders();
            String authToken = BearerTokenManager.generateAuthToken(user);
            return new ResponseEntity<>(authToken, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // Update user's password
    @RequestMapping(value = "/users/{name}/password", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePass(@PathVariable String name,
                                        @RequestBody String password, //FIXME This should be in JSON or something...
                                        HttpServletRequest request) {
        logger.info("User update request for user " + name + " (password: " + password + ")");

            // Authenticate petition
            User user = authenticateRequest(request, name);
            if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

            // Only update if new data is supplied
            User newUser = new User(name, password, user.isPremium());
            if (userRepository.validate(newUser) == null) {
                // Update that data
                User updatedUser = userRepository.update(newUser);
                if (updatedUser != null) {
                    HttpHeaders h = new HttpHeaders();
                    return new ResponseEntity<>(updatedUser, h, HttpStatus.OK);
                } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
                logger.info("User not updated");
                HttpHeaders h = new HttpHeaders();
                return new ResponseEntity<>(user, h, HttpStatus.NOT_MODIFIED);
            }
    }

    // Update user's premium status
    @RequestMapping(value = "/users/{name}/premium", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePremium(@PathVariable String name,
                                           @RequestBody String premiumParam, //FIXME This is an embarrassing quickfix
                                           HttpServletRequest request) {
        logger.info("User update request for user " + name + " (premium: " + premiumParam + ")");

        // Authenticate petition
        User user = authenticateRequest(request, name);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Manually parse request body
        boolean premium;
        if (premiumParam.equalsIgnoreCase("true")) premium = true;
        else if (premiumParam.equalsIgnoreCase("false")) premium = false;
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Only update if new data is supplied
        User newUser = new User(name, null, premium);
        if (premium != user.isPremium()) {
            // Update that data
            User updatedUser = userRepository.update(newUser);
            if (updatedUser != null) {
                HttpHeaders h = new HttpHeaders();
                return new ResponseEntity<>(updatedUser, h, HttpStatus.OK);
            } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            logger.info("User not updated");
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(user, h, HttpStatus.NOT_MODIFIED);
        }
    }

    // DELETE user
    @RequestMapping(value = "/users/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String name,
                                        HttpServletRequest request) {
        logger.info("User delete request for user " + name);

        // Authenticate petition
        User user = authenticateRequest(request, name);
        if (user == null) return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

        // Delete the user
        User deletedUser = userRepository.delete(user);
        if (deletedUser != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(deletedUser, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    /* HELPERS */

    // Extracts the token from the request and ensures the its username matches provided name
    private static User authenticateRequest(HttpServletRequest request, String name) {

        // Get the token from request
        String token = BearerTokenManager.extractAuthToken(request);
        if (token == null) {
            logger.info("Auth fail: No token found");
            return null;
        }

        // Validate the token
        User user = BearerTokenManager.validateAuthToken(token);
        if (user == null || !user.getUsername().equalsIgnoreCase(name)) {
            logger.info("Auth fail: " + (user == null ? "invalid token" : "user not authorized"));
            return null;
        }
        return user;
    }

}
