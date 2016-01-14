package urlshortener2015.eerieblack;

import urlshortener2015.eerieblack.services.registration.RegistrationServer;
import urlshortener2015.eerieblack.services.shortener.ShortenerServer;
import urlshortener2015.eerieblack.services.users.UsersServer;
import urlshortener2015.eerieblack.services.web.WebServer;

public class Main {

    //We will have the following four Micro-Services
    final static String WEB_SERVICE = "web";                    //
    final static String REGISTRATION_SERVICE = "registration";  //Managing the registration of the micro Services in Eureka
    final static String SHORTENER_SERVICE = "shortener";        //Managing the "shorting" of the URI and the Periodical check of reachability
    final static String USERS_SERVICE = "users";                //Managing the Users (find, save, update, delete, list, validate, auth )

    public static void main(String[] args) throws Exception {

        int argsCount = checkArgs(args);

        // If a port is specified, set it
        if (argsCount == 2) System.setProperty("server.port", args[1]);

        // If there are no errors on the arguments, launch the service; show usage otherwise
        if (argsCount > 0) launchService(args[0], args);
        else showUsage();
    }

    private static int checkArgs(String[] args) {
        if ((args.length == 1 || (args.length == 2 && Integer.parseInt(args[1]) >= 1024)) && (
                args[0].equals(WEB_SERVICE)
                || args[0].equals(REGISTRATION_SERVICE)
                || args[0].equals(SHORTENER_SERVICE)
                || args[0].equals(USERS_SERVICE)
        )) return args.length;
        else return  -1;
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar eerie-black.jar <service-name> [<port>]");
        System.out.println("where <port> is > 1024 and <service-name> is one of the following:");
        System.out.printf("  · %s - Discovery server%n", REGISTRATION_SERVICE);
        System.out.printf("  · %s - Web server that serves the frontend and the API%n", WEB_SERVICE);
        System.out.printf("  · %s - Url shortener microservice which actually stores the URIs%n", SHORTENER_SERVICE);
        System.out.printf("  · %s - User management microservice%n", USERS_SERVICE);
    }

    private static void launchService(String serviceName, String[] args) throws Exception {
        switch (serviceName) {
            case WEB_SERVICE: WebServer.main(args); break; //Execute WebServer's main
            case REGISTRATION_SERVICE: RegistrationServer.main(args); break; //Execute RegistrationServer's main
            case SHORTENER_SERVICE: ShortenerServer.main(args); break; //Execute ShortenerServer's main
            case USERS_SERVICE: UsersServer.main(args); break; //Execute UsersServer's main
        }
    }
}
