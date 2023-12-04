package Algorithms;

public class PetersonAlgorithm implements Lock {
    private final boolean[] flag = new boolean[2];
    private volatile int turn;

    @Override
    public void lock(int i) {
        int j = 1 - i; // other process
        flag[i] = true; // "I'm interested"
        turn = j; // "it's your turn"
        while (flag[j] && turn == j) { // while the other is interested
            // wait
        }
    }

    @Override
    public void unlock(int i) {
        flag[i] = false; // "I'm no longer interested"
    }
}
