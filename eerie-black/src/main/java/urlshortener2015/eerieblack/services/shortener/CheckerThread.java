package urlshortener2015.eerieblack.services.shortener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import urlshortener2015.common.domain.ShortURL;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Marcos on 12/01/2016.
 */
@Component
@Scope("prototype")
public class CheckerThread extends Thread{

    @Autowired
    protected ShortURLExtRepository shortURLExtRepository;

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("=========================");
            List<ShortURL> list = shortURLExtRepository.list();
            for(ShortURL l: list){
                System.out.println(l.getHash()+":"+ l.getTarget());
                String url = l.getTarget();
                int myResponseCode = 0;
                try {
                    URL urlTest = new URL(url);
                    HttpURLConnection http = (HttpURLConnection) urlTest.openConnection();
                    myResponseCode = http.getResponseCode();
                    if(200 <= myResponseCode && 300 > myResponseCode ){
                        //Bien
                    }
                    else{
                        //Mal
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //Mal

                } catch (IOException e) {
                    e.printStackTrace();
                    //Mal

                }
            }
            System.out.println("=========================");
        }
    }
}
