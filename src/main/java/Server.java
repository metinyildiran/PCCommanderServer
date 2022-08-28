import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    static ServerSocket socket;
    static Socket clientSocket;

    public static void main(String[] args) {

        try {
            socket = new ServerSocket(1755);

            WaitForMessage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static void WaitForMessage() throws IOException {
        String msg_received;

        clientSocket = socket.accept();       //This is blocking. It will wait.
        DataInputStream DIS = new DataInputStream(clientSocket.getInputStream());

        msg_received = DIS.readUTF();

        if (msg_received.equals("open chrome"))
            Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome " + "https://www.google.com/"});

        WaitForMessage();

//        clientSocket.close();
//        socket.close();
    }
}
