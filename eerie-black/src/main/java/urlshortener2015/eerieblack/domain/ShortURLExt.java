package urlshortener2015.eerieblack.domain;

import urlshortener2015.common.domain.ShortURL;

import java.net.URI;
import java.sql.Date;

/**
 * Created by Marcos on 13/01/2016.
 */
public class ShortURLExt extends ShortURL{
    private Boolean reachable;
    private Date notreachablesince;
    public ShortURLExt(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, String ip,
                    String country, Boolean reachable, Date notreachablesince) {
        super(hash, target, uri, sponsor, created, owner, mode, safe, ip, country);
        this.reachable = reachable;
        this.notreachablesince = notreachablesince;
    }

    public ShortURLExt() {
            super();
    }
}
