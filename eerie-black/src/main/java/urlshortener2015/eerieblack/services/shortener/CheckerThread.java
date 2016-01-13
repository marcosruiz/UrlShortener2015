package urlshortener2015.eerieblack.services.shortener;

import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Marcos on 12/01/2016.
 */
@Component
@Scope("prototype")
public class CheckerThread extends Thread{

    @Override
    public void run() {
        while(true){
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("========YOYO");
        }
    }
}
