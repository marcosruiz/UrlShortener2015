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
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.web.UrlShortenerController;
import urlshortener2015.eerieblack.services.shortener.ShortenerServiceWrapper;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@Profile("web")
@ComponentScan(basePackages = { "urlshortener2015.common.repository", "urlshortener2015.eerieblack.services.shortener" }) // Añadir la ruta de todos los wrappers
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

    @Autowired
    ShortenerServiceWrapper shortenerServiceWrapper;

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

    @Override
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
        // Get url from shortenerService instead of own database
        ShortURL url = shortenerServiceWrapper.getByHash(id);
        if (url != null) return createSuccessfulRedirectToResponse(url);
        else return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand, HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		ShortURL shortURL = shortenerServiceWrapper.postNewURL(url, sponsor, brand);
		if (shortURL != null) {
			// Modify shortURL to point to this web server instead of the shortener microservice
			shortURL = new ShortURL(
					shortURL.getHash(),
					shortURL.getTarget(),
					linkTo(
                            methodOn(UrlShortenerControllerWithLogs.class)
                                    .redirectTo(shortURL.getHash(), null)
                    ).toUri(),
					shortURL.getSponsor(),
					shortURL.getCreated(),
					shortURL.getOwner(),
					shortURL.getMode(),
					shortURL.getSafe(),
					shortURL.getIP(),
					shortURL.getCountry()
			);
			HttpHeaders h = new HttpHeaders();
			h.setLocation(shortURL.getUri());
			return new ResponseEntity<>(shortURL, h, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
	}
}
