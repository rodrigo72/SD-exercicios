import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

        MeasureSelectorQueue<Job> q = new MeasureSelectorQueue<>(20);

        class Entry implements Comparable<Entry> {
            long availableMemory;
            long threadId;

            public Entry(long availableMemory, long threadId) {
                this.availableMemory = availableMemory;
                this.threadId = threadId;
            }

            public int compareTo(Entry e) {
                int result = Long.compare(this.availableMemory, e.availableMemory);
                if (result == 0)
                    return Long.compare(threadId, e.threadId);
                return result;
            }

            public String toString() {
                return String.format("Entry(%d, %d)", availableMemory, threadId);
            }
        }

        List<Entry> available = new ArrayList<>() {
            public boolean add(Entry mt) {
                int index = Collections.binarySearch(this, mt);
                if (index < 0) index = ~index;
                super.add(index, mt);
                return true;
            }
        };

        available.add(new Entry(100, 1));
        available.add(new Entry(20, 2));
        available.add(new Entry(10, 3));
        available.add(new Entry(30, 4));

        for (Entry e : available) {
            System.out.println(e);
        }



    }
}