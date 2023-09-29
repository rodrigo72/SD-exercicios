import Algorithms.BakeryAlgorithm;
import Algorithms.FilterAlgorithm;
import Algorithms.Lock;
import Algorithms.PetersonAlgorithm;
import Utils.BestCounter;
import Utils.MyProcess;

public class Testing {

    private static final BestCounter counter = new BestCounter();
    public static void main(String[] args) {
        // Testing.testPetersonAlgorithm();
        //Testing.testFilterAlgorithm();
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

    public static void testFilterAlgorithm() {
        System.out.println("\nTesting Filter Algorithm:");

        int numberOfProcesses = 5;
        Lock filterAlgorithm = new FilterAlgorithm(numberOfProcesses);

        counter.reset();
        for (int i = 0; i < numberOfProcesses; i++) {
            Thread processThread = new Thread(new MyProcess(filterAlgorithm, i, counter));
            processThread.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int expectedValue = numberOfProcesses * counter.max;
        System.out.println("Expected value: " + expectedValue + "\nResult: " + counter.getValue());
    }

    public static void testBakeryAlgorithm() {
        int numberOfProcesses = 3;
        Lock bakeryAlgorithm = new BakeryAlgorithm(numberOfProcesses);

        counter.reset();
        for (int i = 0; i < numberOfProcesses; i++) {
            Thread processThread = new Thread(new MyProcess(bakeryAlgorithm, i, counter));
            processThread.start();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int expectedValue = numberOfProcesses * counter.max;
        System.out.println("Expected value: " + expectedValue + "\nResult: " + counter.getValue());
    }
}