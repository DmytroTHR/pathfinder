package pathfinder.server;

import pathfinder.client.ClientTask;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;

public class LookupServer extends Thread implements Closeable {
    private ServerSocket socket;
    private String root;
    private static int taskCounter;


    public LookupServer(int port, String root) throws IOException {
        this.socket = new ServerSocket(port);
        this.root = root;
    }

    @Override
    public void run() {
        while (true) {
            try {
                ClientTask task = new ClientTask(socket.accept());
                task.start();
                taskCounter++;
                System.out.println("Task " + taskCounter + " is being executed.");
            } catch (IOException ex) {
                System.err.println("Error running server");
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void close() throws IOException {
        this.socket.close();
    }
}
