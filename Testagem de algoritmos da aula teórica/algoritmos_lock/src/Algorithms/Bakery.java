package Algorithms;

public class Bakery extends Thread implements Lock, Runnable {

    public int threadId;
    public static final int countToThis = 200;
    public static final int numberOfThreads = 5;
    public static volatile int count = 0;

    private static volatile boolean[] choosing = new boolean[numberOfThreads];
    private static volatile int[] ticket = new int[numberOfThreads];

    public Bakery(int id) {
        this.threadId = id;

        for (int i = 0; i < numberOfThreads; i++) {
            choosing[i] = false;
            ticket[i] = 0;
        }
    }

    @Override
    public void run() {
        int scale = 2;
        for (int i = 0; i < countToThis; i++) {
            this.lock(this.threadId);
            count += 1;
            System.out.println("I am " + this.threadId + " and count is: " + count);
            try {
                sleep((int) (Math.random() * scale));
            } catch (InterruptedException e) { /* nothing */ }
            this.unlock(this.threadId);
        }
    }

    @Override
    public void lock(int id) {
        // that means that the current thread (with id = id), is interested in getting into the critical section.
        choosing[id] = true;

        // find the max value and add 1 to get the next available ticket.
        ticket[id] = findMax() + 1;
        choosing[id] = false;

        // System.out.println("Thread " + id + " got ticket in Lock");

        for (int j = 0; j < numberOfThreads; j++) {
            // If the thread j is the current thread go the next thread.
            if (j == id)
                continue;

            // Wait if thread j is choosing right now.
            while (choosing[j]) { /* nothing */ }

            while (ticket[j] != 0 && (ticket[id] > ticket[j] || (ticket[id] == ticket[j] && id > j))) { /* nothing */ }

        }
    }

    @Override
    public void unlock(int id) {
        ticket[id] = 0;
    }

    private int findMax() {
        int m = ticket[0];
        for (int i = 1; i < ticket.length; i++)
            if (ticket[i] > m)
                m = ticket[i];
        return m;
    }
}
