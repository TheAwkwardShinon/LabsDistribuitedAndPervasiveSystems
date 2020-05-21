package server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class App {

//il server non è niente di che.. bastano solo queste cose
    public static void main(String[] args) throws IOException, InterruptedException {
        Server s = ServerBuilder.forPort(8080).addService(new SumImpl()).build();
        s.start();
        s.awaitTermination();

    }
}
