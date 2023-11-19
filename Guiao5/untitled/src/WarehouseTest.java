import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WarehouseTest {

    private final Warehouse warehouse = new Warehouse();
    class Supplier implements Runnable {
        public void run() {
            int quantity = 10;
            for (int i = 0; i < 5; i++) {
                warehouse.supply("item" + i, quantity);
                System.out.println("Supplied item" + i + " with " + quantity + " items");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Consumer implements Runnable {
        public void run() {
            Set<String> itemsToConsume = new HashSet<>();
            for (int i = 0; i < 5; i++) {
                int r = (int) (Math.random() * 5);
                itemsToConsume.add("item" + r);
            }
            System.out.println("Consuming " + itemsToConsume);
            warehouse.consume(itemsToConsume);
            String threadId = String.valueOf(Thread.currentThread().threadId());
            System.out.println("Thread " + threadId + " -> " + warehouse.getMap());
        }
    }

    public static void main(String[] args) {

        WarehouseTest warehouseTest = new WarehouseTest();

        // Supplier thread
        Thread supplierThread = new Thread(warehouseTest.new Supplier());
        supplierThread.start();

        // Consumer threads
        Thread consumerThread = new Thread(warehouseTest.new Consumer());
        consumerThread.start();

        Thread consumerThread2 = new Thread(warehouseTest.new Consumer());
        consumerThread2.start();

        // Wait for threads to finish
        try {
            supplierThread.join();
            consumerThread.join();
            consumerThread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

}
