import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(12345);) {

            while (true) {
                Socket socket = ss.accept();
                System.out.println("Accepted connection from " + socket);

                ClientHandler handler = new ClientHandler(socket);
                Thread thread = new Thread(handler);
                thread.start();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server is shutting down...");
        }
    }
}

