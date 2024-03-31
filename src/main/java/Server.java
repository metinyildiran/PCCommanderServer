import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import commands.MediaKeys;
import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Server {
    private static Tray tray;
    
    public static void main(String[] args) {
        tray = new Tray();

        StartServer();
    }

    private static void StartServer() {
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(1755), 0);
            server.createContext("/command", new CommandHandler());
            server.createContext("/text", new TextHandler());
            server.createContext("/media", new MediaHandler());
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

            os.write(response.getBytes());
            os.flush();
            os.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Request getRequestBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int length; (length = inputStream.read(buffer)) != -1; ) {
            result.write(buffer, 0, length);
        }

        JSONObject jsonObject = new JSONObject(result.toString());

        return new Request(jsonObject.getString("request"));
    }

    private static class CommandHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    String[] command = getRequestBody(exchange.getRequestBody()).request().replaceAll("[,\"]", "").split(" ");

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

    private static class TextHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    String text = getRequestBody(exchange.getRequestBody()).request();

                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);

                    displayTray("Copied to clipboard");
                } else {
                    SendResponse(exchange, "Method Not Allowed", 405);
                }
            } catch (IOException | NullPointerException e) {
                SendResponse(exchange, "Bad Request", 400);
                e.printStackTrace();
                throw new RuntimeException(e);
            } catch (AWTException e) {
                throw new RuntimeException(e);
            }
        }

        public void displayTray(String message) throws AWTException {
            tray.trayIcon.displayMessage("PC Commander Server", message, TrayIcon.MessageType.INFO);
        }
    }

    private static class MediaHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) {
            try {
                if (exchange.getRequestMethod().equals("POST")) {
                    String command = getRequestBody(exchange.getRequestBody()).request();

                    MediaKeys.executeMediaKey(command);

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
