public class Deposit implements Runnable {

    private final Bank bank;

    public Deposit(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++)
            Bank.getInstance().deposit(100);
    }
}
