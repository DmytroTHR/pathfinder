package pathfinder.client;

import java.io.*;
import java.net.Socket;

public class ClientTask extends Thread {
    private final Socket socket;

    public ClientTask(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (socket) {
            while (true) {
                InputStream input = socket.getInputStream();
                BufferedReader readerClient = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writerClient = new PrintWriter(output, true);
                writerClient.println("Ready for new search. (Q - to exit)");

                String data = readerClient.readLine();
                ResultFounder founder = fillFounder(data);
                if (!founder.getMask().isEmpty() || founder.getDepth() > 0) {
                    long found = founder.searchForResult(writerClient);

                    writerClient.println("Found " + found);
                } else if (data.equalsIgnoreCase("q")) {
                    socket.close();
                    return;

                } else {
                    writerClient.println("Wrong input. Try again.");
                    writerClient.println("eg.: d=3; m=*mask");
                }
            }
        } catch (IOException ex) {
            System.err.println("Error when running client task");
            ex.printStackTrace();
        }
    }

    private ResultFounder fillFounder(String data) {
        ResultFounder result = new ResultFounder();

        String[] params = data.split(";");
        if (params.length != 2) {
            return result;
        }

        for (String param : params) {
            if (param.length() < 3) {
                return result;
            }
            param = param.trim();
            String firstTwo = param.substring(0, 2);
            String others = param.substring(2);
            switch (firstTwo.toLowerCase()) {
                case "d=":
                    result.setDepth(Integer.valueOf(others));
                    break;
                case "m=":
                    result.setMask(others);
                    break;
                default:
                    return result;
            }
        }

        return result;
    }
}
