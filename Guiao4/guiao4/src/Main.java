import java.util.Scanner;

public class Main {

    private static void runBarrierExample() {
        final int PARTICIPANTS = 3;
        final Barrier barrier = new Barrier(PARTICIPANTS);

        for (int i = 0; i < PARTICIPANTS; i++) {
            Thread thread = new Thread(() -> {
                try {
                    System.out.println("Thread " + Thread.currentThread().threadId() + " is waiting");
                    barrier.await();
                    System.out.println("Thread " + Thread.currentThread().threadId() + " resumed");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            thread.start();
        }
    }

    private static void runAgreementExample(int numberOfThreads) {

        Agreement agreement = new Agreement(numberOfThreads);
        Thread[] threads = new Thread[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            threads[i] = new Thread(new AgreementRunner(agreement));
            threads[i].start();
        }

        for (int i = 0; i < numberOfThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Press 1 to run Barrier example or 2 to run Agreement example");

        int choice = scanner.nextInt();

        if (choice == 1) {
            runBarrierExample();
        } else if (choice == 2) {
            runAgreementExample(5);
        } else {
            System.out.println("Invalid choice");
        }
    }
}

 /*

    Variáveis de condição

    - Permitem que threads suspendam/ retomem a sua execução dentro de secções críticas, de acordo com uma dada condição

        Condition() -> construtor
        await() -> thread atual fica em espera até que seja notificada para retomar execução
        signal() -> notifica uma thread para resumir a sua execução
        signalAll() -> notifica todas as threads para resumirem a sua execução

    - Uma variável de condição está intrinsecamente licada a um Lock

    - Na invocação do await(), o lock é libertado atomicamente e *a execução da thread é suspendida*

    - Na invocação de signalAll(), todas as threads em espera acordam e competem pelo lock. A thread que o adquire faz
      lock e resume a sua execução.


    Exercício:

    Implemente um classe Barrier que ofereça um método await() cujo objetivo é garantir que cada thread que o invoque
    bloqueie até que o número de threads nesta situação

 */