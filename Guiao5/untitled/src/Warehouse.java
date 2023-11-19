import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


class Warehouse {
    private final Map<String, Product> map =  new HashMap<String, Product>();
    Lock l = new ReentrantLock();

    private class Product {
        Condition isEmpty = l.newCondition();
        int quantity = 0;

        public String toString() {
            return "Product(" + quantity + ")";
        }
    }

    public Map<String, Product> getMap() {
        return this.map;
    }

    private Product get(String item) {
        l.lock();
        try {
            Product p = this.map.get(item);
            if (p != null) return p;
            p = new Product();
            this.map.put(item, p);
            return p;
        }
        finally {
            l.unlock();
        }
    }

    public void supply(String item, int quantity) {
        l.lock();
        try {
            Product p = this.get(item);
            p.quantity += quantity;
            p.isEmpty.signalAll();
        } finally {
            l.unlock();
        }
    }

    public void consume(Set<String> items) {
        l.lock();
        try {
            int maxRetries = 10;
            int retries = 0;

            boolean allAvailable = false;
            while (!allAvailable) {  // waits until all items are available
                allAvailable = true;
                for (String i : items) {
                    Product p = this.get(i);
                    if (p.quantity == 0) {
                        allAvailable = false;
                        retries += 1;
                        p.isEmpty.await();
                        break;
                    }
                }
                if (retries >= maxRetries)
                    break;
            }

            if (retries == maxRetries) {  // starvation detected
                // start greedy version:
                for (String i : items) {
                    Product p = this.get(i);
                    while (p.quantity == 0)  // consumer "reserves" the item
                        p.isEmpty.await();
                    p.quantity--; // consumed
                }
            } else {
                for (String i : items) {
                    this.get(i).quantity -= 1;
                }
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            l.unlock();
        }
    }
}
