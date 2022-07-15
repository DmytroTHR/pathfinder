import pathfinder.server.FileCrawler;
import pathfinder.server.LookupServer;

import java.io.IOException;

public class App {
    static String root;
    static Integer port;

    static void usage() {
        System.err.println("java App -Ddepth=<port> -DrootPath=<rootPath>");
        System.err.println("\t port [1024-65535] - port on which server will run");
        System.err.println("\t rootPath - absolute path where to start from");
        System.exit(-1);
    }

    static boolean fillParams() {
        root = System.getProperty("rootPath");
        String portStr = System.getProperty("port");
        port = portStr == null ? null : Integer.parseInt(portStr);

        return (root != null && root.length() > 0
                && port != null && port > 1024 && port < 65535);
    }

    public static void main(String[] args) {
        if (!fillParams()) {
            usage();
        }

        FileCrawler fileCrawler = new FileCrawler(root);
        new Thread(fileCrawler).start();

        try (LookupServer server = new LookupServer(port, root)) {
            server.start();
            server.join();
        } catch (IOException ex) {
            System.err.println("Server start error. Port: "+port);
            ex.printStackTrace();
        } catch (InterruptedException ex) {
            System.err.println("Server stop error. Port: "+port);
            ex.printStackTrace();
        }


    }

}