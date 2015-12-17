package urlshortener2015.eerieblack.web;

import com.google.common.hash.Hashing;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.web.UrlShortenerController;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.sql.Date;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class UrlShortenerControllerWithLogs extends UrlShortenerController {

	private static final Logger logger = LoggerFactory.getLogger(UrlShortenerControllerWithLogs.class);

	@Override
	@RequestMapping(value = "/{id:(?!link|index).*}", method = RequestMethod.GET)
	public ResponseEntity<?> redirectTo(@PathVariable String id, HttpServletRequest request) {
		logger.info("Requested redirection with hash " + id);
		return super.redirectTo(id, request);
	}

	@Override
	public ResponseEntity<ShortURL> shortener(@RequestParam("url") String url,
			@RequestParam(value = "sponsor", required = false) String sponsor,
			@RequestParam(value = "brand", required = false) String brand, HttpServletRequest request) {
		logger.info("Requested new short for uri " + url);
		return super.shortener(url, sponsor, brand, request);
	}

	@Override
	protected ShortURL createAndSaveIfValid(String url, String sponsor,
											String brand, String owner, String ip) {
		UrlValidator urlValidator = new UrlValidator(new String[] { "http",
				"https" });
		if (urlValidator.isValid(url)) {
			if(brand.equals("")){
				brand = Hashing.murmur3_32()
						.hashString(url, StandardCharsets.UTF_8).toString();
			}
			ShortURL s = shortURLRepository.findByKey(brand);
			if(s == null || s.getTarget()==null){
				//No existe con antelacion: no hay problema
			}else{
				//Existe con antelacion
				if(!s.getTarget().equals(url)){
					//las uris no coinciden
					return null;
				}
			}
			ShortURL su = new ShortURL(brand, url,
					linkTo(
							methodOn(UrlShortenerController.class).redirectTo(
									brand, null)).toUri(), sponsor, new Date(
					System.currentTimeMillis()), owner,
					HttpStatus.TEMPORARY_REDIRECT.value(), true, ip, null);
			return shortURLRepository.save(su);
		} else {
			return null;
		}
	}
}
