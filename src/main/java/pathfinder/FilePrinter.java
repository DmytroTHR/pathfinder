package pathfinder;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.ConcurrentLinkedQueue;

public class FilePrinter implements Runnable, Closeable {
    private ConcurrentLinkedQueue<Path> pathsToPrint;
    private boolean stopIt;
    private int counter;

    public FilePrinter() {
        pathsToPrint = new ConcurrentLinkedQueue<>();
        stopIt = false;
        counter = 0;
    }

    public void printPath(Path p) {
        this.pathsToPrint.add(p);
    }

    @Override
    public void run() {
        while (!pathsToPrint.isEmpty() || !stopIt) {
            Path curPrint = pathsToPrint.poll();
            if (curPrint != null) {
                counter++;
                System.out.println(counter + ":\t" +curPrint.toAbsolutePath());
            }
        }
    }

    @Override
    public void close() {
        stopIt = true;
    }
}
