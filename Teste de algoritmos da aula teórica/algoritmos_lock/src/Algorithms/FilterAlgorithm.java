package Algorithms;

public class FilterAlgorithm implements Lock {

    private final int[] victim;
    private final int[] level;
    private final int maxProcesses;

    public FilterAlgorithm(int maxProcesses) {
        this.maxProcesses = maxProcesses;
        this.level = new int[maxProcesses];
        this.victim = new int[maxProcesses];

        for (int i = 0; i < this.maxProcesses; i++) {
            this.level[i] = 0;
            this.victim[i] = 0;
        }
    }

    @Override
    public void lock(int id) {
        for (int l = 1; l < this.maxProcesses; l++) {
            level[id] = l;
            victim[l] = id;
            while(existsHigherLevelOrEqualVictim(id, l)) {
                // wait
            }
        }
    }

    @Override
    public void unlock(int id) {
        this.level[id] = 0;
    }

    private boolean existsHigherLevelOrEqualVictim(int id, int level) {
        for (int i = 0; i < this.maxProcesses; i++) {
            if (i != id && (this.level[i] >= level && victim[level] == id)) {
                return true;
            }
        }
        return false;
    }
}
