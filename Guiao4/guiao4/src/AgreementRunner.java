public class AgreementRunner implements Runnable {

    private final Agreement agreement;

    public AgreementRunner(Agreement agreement) {
        this.agreement = agreement;
    }
    @Override
    public void run() {
        try {
            int choice = (int) (Math.random() * 100); // Simulating different choices
            int result = this.agreement.propose(choice);
            System.out.println("Thread " + Thread.currentThread().threadId() +
                    " proposed " + choice + " and the agreement is " + result);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
