import javax.accessibility.AccessibleContext;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Bank {

    private final ReentrantReadWriteLock lockBanco = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lockBanco.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lockBanco.writeLock();


    private static class Account {
        public ReentrantReadWriteLock lockAccount = new ReentrantReadWriteLock();
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private int nextId = 0;

    public int get_n_accounts() {
        // read a shared resource without modifying it
        this.readLock.lock(); // multiple threads can hold the read lock simultaneously => concurrent reading

        // if a thread requests a read lock and a write lock is held, the thread will be blocked until the
        // write lock is released.
        // there is no priority for readers or writers : FIFO

        try {
            return map.size();
        } finally {
            this.readLock.unlock();
        }
    }

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        this.writeLock.lock(); // only one thread can hold the write lock at a time, and no other thread (read or write)
        // can access the resource concurrently/ simultaneously

        // if a thread requests a write lock and there are existing read locks or another write lock, the thread will be
        // blocked until the lock can be acquired.

        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            this.writeLock.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        this.writeLock.lock(); // exclusive access to the map of accounts; prevents other threads from modifying the map
        Account c;

        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.lockAccount.readLock().lock(); // lock associated with the account c; no other operations can be performed while its
                                  // balance is being retrieved (even though it was removed from the map)
        } finally {
            this.writeLock.unlock();
        }

        try {
            return c.balance();
        } finally {
            c.lockAccount.readLock().unlock(); // unlock after returning the balance
        }

    }

    // account balance; 0 if no such account
    public int balance(int id) {
        this.readLock.lock();
        Account c;

        try {
            c = map.get(id);
            if (c == null)
                return 0;
            c.lockAccount.readLock().lock();
        } finally {
            this.readLock.unlock();
        }

        try {
            return c.balance();
        } finally {
            c.lockAccount.readLock().unlock();
        }
    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        this.readLock.lock();
        Account c;

        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.writeLock().lock();
        } finally {
            this.readLock.unlock();
        }

        try {
            return c.deposit(value);
        } finally {
            c.lockAccount.writeLock().unlock();
        }

    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        this.readLock.lock();
        Account c;

        try {
            c = map.get(id);
            if (c == null)
                return false;
            c.lockAccount.writeLock().lock();
        } finally {
            this.readLock.unlock();
        }

        try {
            return c.withdraw(value);
        } finally {
            c.lockAccount.writeLock().unlock();
        }
    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        this.readLock.lock();
        Account cFrom, cto;

        try {
            cFrom = map.get(from);
            cto = map.get(to);
            if (cFrom == null || cto ==  null)
                return false;

            // consistent and deterministic lock ordering to avoid deadlocks, prevent circular waiting
            if (from < to) {
                cFrom.lockAccount.writeLock().lock();
                cto.lockAccount.writeLock().lock();
            } else {
                cto.lockAccount.writeLock().lock();
                cFrom.lockAccount.writeLock().lock();
            }

            /*
            example of a deadlock if the if-statement was not used

            { cFrom.lockAccount.lock();
              cto.lockAccount.writeLock().lock(); }

            Simultaneously:
            Thread 1: bank.transfer(1,2,100);
            Thread 2: bank.transfer(2,1,50);

            1. Thread 1 acquires a read lock and then acquires a write lock on account 1
            2. Thread 2 acquires a read lock and then acquires a write lock on account 2
            3. Now both threads are holding a write lock on one account and trying to acquire a write lock of the other
               account, creating a deadlock -- since neither thread will release its existing lock until it acquires the
               second lock, both threads will be stuck.
             */

        } finally {
            this.readLock.unlock();
        }

        try {
            try {
                if (!cFrom.withdraw(value))
                    return false;
            } finally {
                cFrom.lockAccount.writeLock().unlock();
            }
            return cto.deposit(value);
        } finally {
            cto.lockAccount.writeLock().unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist
    public int totalBalance(int[] ids) {
        int total = 0;
        this.readLock.lock();

        Account[] accounts = new Account[ids.length];

        ids = Arrays.stream(ids).sorted().toArray();

        try {
            for (int i = 0; i < ids.length; i++) {
                Account c = map.get(ids[i]); // sorted ids
                if (c == null)
                    return 0;
                accounts[i] = c;
            }

            for (int i = 0; i < ids.length; i++) { // no account can be closed while the total balance "snapshot" is being taken
                accounts[i].lockAccount.readLock().lock();
            }
        } finally {
            this.readLock.unlock();
        }

        for (int i = 0; i < ids.length; i++) {
            try {
                total += accounts[i].balance();
            } finally {
                accounts[i].lockAccount.readLock().unlock();
            }
        }

        return total;
    }
}