package Utils;

import Algorithms.Lock;

public class MyProcess implements Runnable {
    private final Lock lock;
    private final int id;
    private final Runnable function;

    public MyProcess(Lock lock, int id, Runnable function) {
        this.lock = lock;
        this.id = id;
        this.function = function;
    }

    @Override
    public void run() {
        lock.lock(this.id);
        function.run();
        lock.unlock(this.id);
    }
}
