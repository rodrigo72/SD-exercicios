import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bank {

    // private static final Lock lock_bank = new ReentrantLock();
    private static class Account {
        Lock lock = new ReentrantLock();
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() {
            return balance;
        }
        boolean deposit(int value) {
            try {
                lock.lock();
                balance += value;
                return true;
            } finally {
                lock.unlock();
            }
        }
        boolean withdraw(int value) {
            try {
                lock.lock();
                if (value > balance)
                    return false;
                balance -= value;
                return true;
            } finally {
                lock.unlock();
            }
        }
    }

    // Bank slots and vector of accounts
    private int slots;
    private Account[] av;

    public Bank(int n) {
        slots=n;
        av=new Account[slots];
        for (int i=0; i<slots; i++) av[i] = new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        return av[id].balance();
    }

    // Deposit
    public boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].deposit(value);
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        return av[id].withdraw(value);
    }

    public boolean transfer(int from, int to, int value) {
        av[from].lock.lock();
        av[to].lock.lock();
        if (!this.withdraw(from, value)) return false;
        this.deposit(to, value);
        av[from].lock.unlock();
        av[to].lock.unlock();

        return true;
    }

    public int totalBalance() {
        int sum = 0;
        // lock_bank.lock();
        for (Account account : av) {
            sum += account.balance();
        }
        // lock_bank.unlock();
        return sum;

        //return Arrays.stream(this.av).mapToInt(Account::balance).sum();
    }
}