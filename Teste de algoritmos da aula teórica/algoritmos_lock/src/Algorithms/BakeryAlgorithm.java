package Algorithms;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BakeryAlgorithm implements Lock {

    private AtomicBoolean[] choosing; // could be volatile instead (?)
    private AtomicInteger[] number;
    private int maxProcesses;

    public BakeryAlgorithm(int maxProcesses) {
        this.maxProcesses = maxProcesses;
        this.choosing = new AtomicBoolean[maxProcesses];
        this.number = new AtomicInteger[maxProcesses];

        for (int i = 0; i < maxProcesses; i++) {
            choosing[i] = new AtomicBoolean(false);
            number[i] = new AtomicInteger(0);
        }
    }

    @Override
    public void lock(int id) {
        choosing[id] = new AtomicBoolean(true);
        int maxNumber = 0;

        for (int i = 0; i < this.maxProcesses; i++) {
            int currentNumber = number[i].get();
            maxNumber = Math.max(maxNumber, currentNumber);
        }

        number[id].set(maxNumber + 1);
        choosing[id] = new AtomicBoolean(false);

        for (int i = 0; i < this.maxProcesses; i++) {
            while (choosing[i].get()) {
                // wait while other thread is choosing
            }

            while ((number[i].get() != 0) && (number[i].get() < number[id].get() ||
                    (number[i].get() == number[id].get() && i < id))) {
                // wait while other threads have a smaller number or same number but higher index
            }
        }
    }

    @Override
    public void unlock(int id) {
        this.number[id].set(0);
    }
}
