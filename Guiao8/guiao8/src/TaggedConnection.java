import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

public class TaggedConnection implements AutoCloseable {

    // a message with tag 0 is not replied to
    // messages with odd tags are replied to, with the same tag
    // messages with even tags are streamed (terminated by an empty message)
    public static class Frame {
        public final int tag;
        public final byte[] data;
        public Frame(int tag, byte[] data) {
            this.tag = tag;
            this.data = data;
        }

        public void serialize(DataOutputStream out) throws IOException {
            out.writeInt(this.tag);
            out.writeInt(this.data.length);
            out.write(this.data);
        }

        public static Frame deserialize(DataInputStream in) throws IOException {
            int tag = in.readInt();
            int length = in.readInt();
            byte[] data = new byte[length];
            in.readFully(data);
            return new Frame(tag, data);
        }

    }

    private Socket socket;
    private ReentrantLock sendLock;
    private ReentrantLock receiveLock;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;

    public TaggedConnection(Socket s) throws IOException {
        this.socket = s;
        this.sendLock = new ReentrantLock();
        this.receiveLock = new ReentrantLock();
        this.dataInputStream = new DataInputStream(this.socket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
    }

    public void send(Frame frame) throws IOException {
        try {
            this.sendLock.lock();
            frame.serialize(this.dataOutputStream);
            this.dataOutputStream.flush();
        } finally {
            this.sendLock.unlock();
        }
    }

    public void send(int tag, byte[] data) throws IOException {
        Frame frame = new Frame(tag, data);
        try {
            this.sendLock.lock();
            frame.serialize(this.dataOutputStream);
            this.dataOutputStream.flush();
        } finally {
            this.sendLock.unlock();
        }
    }

    public Frame receive() throws IOException {
        try {
            this.receiveLock.lock();
            return Frame.deserialize(this.dataInputStream);
        } finally {
            this.receiveLock.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        this.dataInputStream.close();
        this.dataOutputStream.close();
        this.socket.close();
    }
}
