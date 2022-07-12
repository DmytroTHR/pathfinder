import pathfinder.FileCrawler;
import pathfinder.FilePrinter;

import java.nio.file.Path;
import java.util.List;

public class App {
    static String root;
    static Integer depth;
    static String mask;

    static void usage() {
        System.err.println("java App -DrootPath=<rootPath> -Ddepth=<depth> -Dmask=<mask>");
        System.err.println("\t rootPath - absolute path where to start from");
        System.err.println("\t depth - depth where to look for data (non-negative)");
        System.err.println("\t mask - glob formatted pattern of path to find");
        System.exit(-1);
    }

    static boolean fillParams() {
        root = System.getProperty("rootPath");
        mask = System.getProperty("mask");
        String depthString = System.getProperty("depth");
        depth = depthString==null ? null : Integer.parseInt(depthString);

        return (root != null && root.length() > 0
                && depth != null && depth >= 0
                && mask != null && mask.length() > 0);
    }

    public static void main(String[] args) {
        if (!fillParams()) {
            usage();
        }

        FilePrinter fp = new FilePrinter();
        Thread printer = new Thread(fp);
        printer.start();
        FileCrawler fCrawler = new FileCrawler(root, depth, mask);
        fCrawler.setPrinter(fp);
        List<Path> results = fCrawler.findFilesThatMatch();
        fp.close();
        System.out.println("found:\t"+results.size());
        try {
            printer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}