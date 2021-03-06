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
            for(ShortURL su: list){
                System.out.println(su.getHash() + ":" + su.getTarget() + ":" + su.getMode());
                String url = su.getTarget();
                int myResponseCode = 0;
                Integer actualMode = su.getMode();
                Integer badMode = new Integer(50);
                Integer goodMode = new Integer(307);
                try {
                    URL urlTest = new URL(url);
                    HttpURLConnection http = (HttpURLConnection) urlTest.openConnection();
                    myResponseCode = http.getResponseCode();
                    if(200 <= myResponseCode && 300 > myResponseCode ){
                        //Bien
                        if(actualMode.equals(badMode)){
                            shortURLExtRepository.isReachable(su);
                        }
                    }
                    else{
                        //Mal
                        if(actualMode.equals(goodMode)){
                            shortURLExtRepository.isNotReachable(su);
                        }
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    //Mal
                    if(actualMode.equals(goodMode)){
                        shortURLExtRepository.isNotReachable(su);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    //Mal
                    if(actualMode.equals(goodMode)){
                        shortURLExtRepository.isNotReachable(su);
                    }
                }
            }
            System.out.println("=========================");
        }
    }
}
