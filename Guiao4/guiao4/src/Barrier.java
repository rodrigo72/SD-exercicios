import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Barrier {

    private final int max;
    private int current;
    private int round;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public Barrier(int n) {
        this.max = n;
        this.current = 0;
        this.round = 0;
    }

    public void await() throws InterruptedException {
        this.lock.lock();
        try {
            this.current += 1;
            if (this.current == this.max) {
                this.current = 0;
                this.round += 1;
                condition.signalAll();
            } else {
                int thread_round = this.round;
                while (this.round == thread_round) {
                    condition.await();
                }
            }
        } catch (InterruptedException e) {
            throw new InterruptedException();
        } finally {
            this.lock.unlock();
        }
    }
}
