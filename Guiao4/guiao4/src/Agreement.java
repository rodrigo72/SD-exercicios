import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Agreement {

    private final ReentrantLock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
    private final int max;
    private int round;
    private int choice;
    private final List<Integer> choices = new ArrayList<>();

    public Agreement (int n) {
        this.max = n;
        this.round = 0;
    }

    int propose (int choice) throws InterruptedException {
        this.lock.lock();
        try {
            this.choices.add(choice);
            if (this.choices.size() >= this.max) {
                this.round++;
                this.choice = this.choices.stream().max(Integer::compare).get();
                this.choices.clear();
                this.condition.signalAll();
            } else {
                int saved_round = this.round;
                while (this.round == saved_round) {
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            return this.choice;
        } finally {
            this.lock.unlock();
        }
    }
}
