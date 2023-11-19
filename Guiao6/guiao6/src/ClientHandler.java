import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Lock writeLock = new ReentrantReadWriteLock().writeLock();
    private final Lock readLock = new ReentrantReadWriteLock().readLock();
    private static int globalSum = 0;
    private static int globalCounter = 0;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(this.clientSocket.getOutputStream(), true);

            String number;
            int currentSum = 0;
            int counter = 0;
            while ((number = in.readLine()) != null) {
                try {
                    int n = Integer.parseInt(number);
                    currentSum += n;
                    out.println(currentSum);
                    counter++;
                    try {
                        this.writeLock.lock();
                        globalSum += n;
                        globalCounter++;
                    } finally {
                        this.writeLock.unlock();
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            if (counter == 0)
                out.println("No numbers were provided");
            else
                try {
                    this.readLock.lock();
                    out.println((double) globalSum / globalCounter);
                } finally {
                    this.readLock.unlock();
                }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                this.clientSocket.shutdownOutput();
                this.clientSocket.shutdownInput();
                this.clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}