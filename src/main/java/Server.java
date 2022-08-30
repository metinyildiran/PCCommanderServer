import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Server {
    static ServerSocket socket;
    static Socket clientSocket;

    public static void main(String[] args) {

        new Tray();

        try {
            socket = new ServerSocket(1755);

            WaitForMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void WaitForMessage() throws IOException {
        clientSocket = socket.accept();       //This is blocking. It will wait.
        DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());

        String incomingCommand = DIS.readUTF();

        System.out.println(incomingCommand);

        Runtime.getRuntime().exec(incomingCommand.replaceAll("[+,\"]", ""));

        WaitForMessage();

//        clientSocket.close();
//        socket.close();
    }
}
