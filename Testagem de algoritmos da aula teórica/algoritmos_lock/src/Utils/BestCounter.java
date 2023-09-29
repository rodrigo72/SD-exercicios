package Utils;

public class BestCounter implements Runnable {
    private int value = 0;
    public void increment() { this.value++; }
    public int getValue() { return this.value; }
    public final int max = 100;

    @Override
    public void run() {
        for (int i = 0; i < this.max; i++) {
            this.increment();
        }
    }

    public void reset() {
        this.value = 0;
    }
}
