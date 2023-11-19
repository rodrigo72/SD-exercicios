import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Demultiplexer implements AutoCloseable {

    private TaggedConnection conn;
    private Map<Integer, Entry> map;
    private ReentrantLock l;

    private class Entry {
        int n_waiting;
        Queue<byte[]> queue;
        Condition condition;

        public Entry() {
            this.n_waiting = 0;
            this.queue = new ArrayDeque<>();
            this.condition = l.newCondition();
        }
    }

    public Demultiplexer (TaggedConnection conn) {
        this.conn = conn;
        this.map = new HashMap<>();
        this.l = new ReentrantLock();
    }

    public void start() {
        new Thread(() -> {
            while (true) {
                try {
                    TaggedConnection.Frame frame = this.conn.receive();
                    this.l.lock();

                    Entry e = this.map.get(frame.tag);
                    if (e == null) {
                        e = new Entry();
                        this.map.put(frame.tag, e);
                    }
                    e.queue.add(frame.data);
                    e.condition.signal();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    this.l.unlock();
                }
            }
        }).start();
    }

    public void sendFrame (TaggedConnection.Frame frame) throws IOException {
        this.conn.send(frame);
    }

    public void send(int tag, byte[] data) throws IOException {
        this.conn.send(tag, data);
    }

    // blocks thread until a message with the given tag is received
    public byte[] receive(int tag) throws IOException, InterruptedException {
        try {
            this.l.lock();
            Entry e = this.map.get(tag);
            if (e == null) {
                e = new Entry();
                map.put(tag, e);
            }

            e.n_waiting++;

            while (true) {
                if (!e.queue.isEmpty()) {
                    e.n_waiting--;
                    byte[] reply = e.queue.poll();
                    if (e.n_waiting == 0 && e.queue.isEmpty())
                        this.map.remove(tag);
                    return reply;
                } else {
                    e.condition.await();
                }
            }

        } finally {
            this.l.unlock();
        }
    }

    @Override
    public void close() throws Exception {
        this.conn.close();
    }
}
