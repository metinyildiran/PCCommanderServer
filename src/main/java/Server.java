import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    public static void main(String[] args) {

        new Tray();

        StartServer();
    }

    private static void StartServer() {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(1755), 0);
            server.createContext("/command", new MyHandler());
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static void SendResponse(HttpExchange exchange, String response, int httpCode) {
        try {
            exchange.sendResponseHeaders(httpCode, response.length());
            OutputStream os = exchange.getResponseBody();

            System.out.println(response);

            os.write(response.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Command getRequestBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }

        JSONObject jsonObject = new JSONObject(result.toString());

        return new Command(jsonObject.getString("command"));
    }

    private static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    String[] command = getRequestBody(exchange.getRequestBody()).command().replaceAll("[,\"]", "").split(" ");

                    final List<String> commands = new ArrayList<>(Arrays.asList(command));

                    ProcessBuilder pb = new ProcessBuilder(commands).inheritIO();
                    pb.start();

                    SendResponse(exchange, "Success", 200);
                } else {
                    SendResponse(exchange, "Method Not Allowed", 405);
                }
            } catch (IOException | NullPointerException e) {
                SendResponse(exchange, "Bad Request", 400);
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
