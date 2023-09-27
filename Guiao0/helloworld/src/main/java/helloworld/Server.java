package helloworld;

import io.grpc.BindableService;
import io.grpc.Grpc;
import io.grpc.InsecureServerCredentials;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.Executors;

// executar com: mvn exec:java -Dexec.mainClass="helloworld.Server"
public class Server {

    public static void main(String[] args) throws Exception {
        Grpc.newServerBuilderForPort(12345, InsecureServerCredentials.create())
                .addService(new BankService(new Bank())
                .executor(Executors.newSingleThreadExecutor())
                .build().start().awaitTermination();
    }
}
