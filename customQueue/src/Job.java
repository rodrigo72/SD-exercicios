public class Job implements Measurable {
    private final long requiredMemory;
    public Job(long requiredMemory) {
        this.requiredMemory = requiredMemory;
    }

    public long measure() {
        return this.requiredMemory;
    }

    public String toString() {
        return Long.toString(this.requiredMemory);
    }
}
