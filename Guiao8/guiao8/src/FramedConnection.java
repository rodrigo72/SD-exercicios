import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FramedConnection implements AutoCloseable {

    private Socket socket;
    private ReentrantLock sendLock;
    private ReentrantLock receiveLock;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public FramedConnection(Socket s) throws IOException {
        this.socket = s;
        this.sendLock = new ReentrantLock();
        this.receiveLock = new ReentrantLock();
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void send(byte[] data) throws IOException {
        try {
            this.sendLock.lock();
            this.dataOutputStream.writeInt(data.length);
            this.dataOutputStream.write(data);
            this.dataOutputStream.flush();
        } finally {
            this.sendLock.unlock();
        }
    }

    public byte[] receive() throws IOException {
        try {
            this.receiveLock.lock();
            int length = this.dataInputStream.readInt();
            byte[] data = new byte[length];
            this.dataInputStream.readFully(data);
            return data;
        } finally {
            this.receiveLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        this.dataOutputStream.close();
        this.dataInputStream.close();
        this.socket.close();
    }
}
