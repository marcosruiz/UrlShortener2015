package urlshortener2015.eerieblack.services.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.eerieblack.auth.AuthTokenManager;
import urlshortener2015.eerieblack.domain.User;
import urlshortener2015.eerieblack.services.users.UsersServiceWrapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Profile("web")
@ComponentScan(basePackages = { "urlshortener2015.common.repository", "urlshortener2015.eerieblack.services.users" }) // Añadir la ruta de todos los wrappers
public class UsersWebController {

    private static final Logger logger = LoggerFactory.getLogger(UsersWebController.class);

    @Autowired
    UsersServiceWrapper usersServiceWrapper;

    // GET users list
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ResponseEntity<?> getUser(@RequestParam(value = "limit", required = false) Long limit,
                                     @RequestParam(value = "offset", required = false) Long offset,
                                     HttpServletRequest request) {
        logger.info("Requested users list starting on " + offset + " with limit " + limit);
        List<User> usersList = usersServiceWrapper.getUsersList(limit, offset);
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
        logger.info("Requested user creation for name " + name);
        User newUser = usersServiceWrapper.postNewUser(name, pass, premium);
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
        logger.info("Requested auth token for name " + name);
        String token = usersServiceWrapper.generateAuthToken(name, pass);
        if (token != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(token, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    // Update user's password
    @RequestMapping(value = "/users/{name}/password", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePass(@PathVariable String name,
                                        @RequestBody String password, //FIXME This should be in JSON or something...
                                        HttpServletRequest request) {
        logger.info("User update request for user " + name + " (password: " + password + ")");

        // Get the token from request
        String token = AuthTokenManager.extractAuthToken(request);
        if (token == null) {
            logger.info("Auth fail: No token found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Validate the token
        User user = AuthTokenManager.validateAuthToken(token);
        if (user == null || !user.getUsername().equalsIgnoreCase(name)) {
            logger.info("Auth fail: " + (user == null ? "invalid token" : "user not authorized"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Update data
        User updatedUser = usersServiceWrapper.updateUserPass(name, password, token);
        if (updatedUser != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(updatedUser, h,
                    updatedUser.equals(user) ? HttpStatus.NOT_MODIFIED : HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // Update user's premium status
    @RequestMapping(value = "/users/{name}/premium", method = RequestMethod.PUT)
    public ResponseEntity<?> updatePremium(@PathVariable String name,
                                           @RequestBody String premiumParam, //FIXME This is an embarrassing quickfix
                                           HttpServletRequest request) {
        logger.info("User update request for user " + name + " (premium: " + premiumParam + ")");

        // Get the token from request
        String token = AuthTokenManager.extractAuthToken(request);
        if (token == null) {
            logger.info("Auth fail: No token found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Validate the token
        User user = AuthTokenManager.validateAuthToken(token);
        if (user == null || !user.getUsername().equalsIgnoreCase(name)) {
            logger.info("Auth fail: " + (user == null ? "invalid token" : "user not authorized"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Manually parse request body
        boolean premium;
        if (premiumParam.equalsIgnoreCase("true")) premium = true;
        else if (premiumParam.equalsIgnoreCase("false")) premium = false;
        else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        User updatedUser = usersServiceWrapper.updateUserPremium(name, premium, token);
        if (updatedUser != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(updatedUser, h,
                    updatedUser.equals(user) ? HttpStatus.NOT_MODIFIED : HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    // DELETE user
    @RequestMapping(value = "/users/{name}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable String name,
                                        HttpServletRequest request) {
        logger.info("User delete request for user " + name);

        // Get the token from request
        String token = AuthTokenManager.extractAuthToken(request);
        if (token == null) {
            logger.info("Auth fail: No token found");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Validate the token
        User user = AuthTokenManager.validateAuthToken(token);
        if (user == null || !user.getUsername().equalsIgnoreCase(name)) {
            logger.info("Auth fail: " + (user == null ? "invalid token" : "user not authorized"));
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Delete the user
        User deletedUser = usersServiceWrapper.deleteUser(name, token);
        if (deletedUser != null) {
            HttpHeaders h = new HttpHeaders();
            return new ResponseEntity<>(deletedUser, h, HttpStatus.OK);
        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

}
