package urlshortener2015.eerieblack.services.shortener;

import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.repository.ShortURLRepository;

import java.util.List;

/**
 * Created by Marcos on 13/01/2016.
 */
public interface ShortURLExtRepository extends ShortURLRepository {
    public List<ShortURL> list();
}
