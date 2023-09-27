package helloworld;

import io.grpc.stub.StreamObserver;

import javax.security.auth.login.AccountException;
import java.security.cert.CertPathBuilder;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public class BankService {

    private final Bank bank;

    public BankService(Bank bank) {
        this.bank = bank;
    }

    public void createAccount (accountID request, StreamObserver<accountID> responseObserver) {
        int accountID = this.bank.createAccount(request.getBalance());
        System.out.println("Account created");
        responseObserver.onNext(
                accountID.newBuilder().serID(accountID).build()
        );
        responseObserver.onCompleted();
    }


    public CertPathBuilder executor(ExecutorService executorService) {
        return null;
    }
}
