package urlshortener2015.eerieblack.services.shortener;

import urlshortener2015.common.domain.ShortURL;
import urlshortener2015.common.repository.ShortURLRepository;

import java.sql.Date;
import java.util.List;

/**
 * Created by Marcos on 13/01/2016.
 */
public interface ShortURLExtRepository extends ShortURLRepository {
    public List<ShortURL> list();
    public boolean isNotReachable(ShortURL su);
    public boolean isReachable(ShortURL su);
    public Date notReachableSince(String id);
}
