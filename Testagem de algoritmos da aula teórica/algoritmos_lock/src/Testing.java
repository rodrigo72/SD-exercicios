import Algorithms.BakeryAlgorithm;
import Algorithms.FilterAlgorithm;
import Algorithms.Lock;
import Algorithms.PetersonAlgorithm;
import Algorithms.Bakery;
import Utils.BestCounter;
import Utils.MyProcess;

public class Testing {

    private static final BestCounter counter = new BestCounter();
    public static void main(String[] args) {
        // Testing.testPetersonAlgorithm();
        Testing.testBakeryAlgorithm();
    }

    public static void testPetersonAlgorithm() {
        System.out.println("\nTesting Peterson's Algorithm:");
        Lock petersonAlgorithm = new PetersonAlgorithm();

        counter.reset();
        Thread t1 = new Thread(new MyProcess(petersonAlgorithm, 0, counter));
        Thread t2 = new Thread(new MyProcess(petersonAlgorithm, 1, counter));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int expectedValue = counter.max * 2;
        System.out.println("Expected value: " + expectedValue + "\nResult: " + counter.getValue());
    }

    public static void testBakeryAlgorithm() {
        Thread[] threads = new Thread[Bakery.numberOfThreads];

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(new Bakery(i));
            threads[i].start();
        }

        // wait all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("\nCount is: " + Bakery.count);
        System.out.println("\nExpected was: " + (Bakery.countToThis * Bakery.numberOfThreads));
    }
}