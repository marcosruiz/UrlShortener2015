package urlshortener2015.eerieblack;

import org.springframework.boot.SpringApplication;
import urlshortener2015.eerieblack.services.WebServer;

public class Main {

    final static String WEB_SERVICE = "web";
    final static String REGISTRATION_SERVICE = "registration";

    public static void main(String[] args) {

        int argsCount = checkArgs(args);

        // If a port is specified, set it
        if (argsCount == 2) System.setProperty("server.port", args[1]);

        // If there are no errors on the arguments, launch the service; show usage otherwise
        if (argsCount > 0) launchService(args[0], args);
        else showUsage();
    }

    private static int checkArgs(String[] args) {
        if ((args.length == 1 || (args.length == 2 && Integer.parseInt(args[1]) >= 1024)) && (
                args[0].equals(WEB_SERVICE) || args[0].equals("registration")
        )) return args.length;
        else return  -1;
    }

    private static void showUsage() {
        System.out.println("Usage: java -jar eerie-black.jar <service-name> [<port>]");
        System.out.println("where <port> is > 1024 and <service-name> is one of the following:");
        System.out.printf("  \'%s\'%n", WEB_SERVICE);
        // System.out.printf("  \'%s\'%n", REGISTRATION_SERVICE);
    }

    private static void launchService(String serviceName, String[] args) {
        if (serviceName.equals(WEB_SERVICE)) {
            SpringApplication.run(WebServer.class, args);
        }
    }
}
