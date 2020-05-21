package client;

import com.classes.grpc.SumGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.classes.grpc.Services.*;
import io.grpc.stub.StreamObserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class Client  {


    /* nota bene : in questo caso la chiamata remota è sincrona quindi l'utilizzo di onnext e oncompleted è nascosto all'utente.
    In caso di utilizzo asincrono questi metodi vanno implementati dal client. -->  guarda metodo sotto questo.
    */

    public static void synchCallSimpleSum(int first, int second){
        //uso quel metodo anche se deprecato perchè lavorando in locale non ha senso creare overhead cifrando il canale di comunicazione
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext(true).build();

        //creo un nuovo stub per il canale di comunicazione. Lo stub è collegato ad un servizio
        SumGrpc.SumBlockingStub stub = SumGrpc.newBlockingStub(channel);

        //costruisco l'oggetto che costituisce i nostri parametri per la procedura da chiamare
        Request request = Request.newBuilder().setFirst(first).setSecond(second).build();

        //chiamo la procedura in remoto e salvo la risposta d nell'oggetto Response
        Response response = stub.simpleSum(request);

        //printo il risultato della somma
        System.out.println(response);

        //importante chiudere alla fine il canale
        channel.shutdown();
    }


    public static void asynchCallRepeatedSum(int num, int repeatitions) throws InterruptedException {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext(true).build();

        //nb: los tub non è più bloccante come sopra
        SumGrpc.SumStub stub = SumGrpc.newStub(channel);

        RepeatedRequest request = RepeatedRequest.newBuilder().setNumberOfRepetitions(repeatitions).setNum(num).build();

        //come detto sopra.. nel caso asynch dobbiamo crearci noi i nostri onnext oncomplete e onerror
        //nota bene questi metodi qui sotto sono rpc chiamate dal server in remoto :)
        stub.repeatedSum(request, new StreamObserver<Response>(){

            //chiamata dal server quando ha immesso un mex
            public void onNext(Response response){
                System.out.println(response);
            }
            //chiamata dal server quando ha finito di inviare mex
            public void onCompleted(){
                channel.shutdown();
            }
            //se c'è qualche errore
            public void onError(Throwable throwable){
                System.err.println("ERROR: "+ throwable);

            }

        });

        channel.awaitTermination(10, TimeUnit.SECONDS); //molto importante altrimenti va tutto a puittane .. ricordati che
        // stai aspettando uno stream di risposte dal server. Senza di cio il programma termina prima di ottenere
        //una risposta dal server
    }




    public static void asynchCallStreamSum() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080").usePlaintext(true).build();
        SumGrpc.SumStub stub = SumGrpc.newStub(channel);


        //chiamo il metodo del server in remoto e definisco i metodi che il server chiamerà in remoto
        StreamObserver serverStream = stub.streamSum(new StreamObserver<Response>(){

            //chiamata dal server quando ha immesso un mex
            public void onNext(Response response){
                System.out.println(response);
            }
            //chiamata dal server quando ha finito di inviare mex
            public void onCompleted(){
                channel.shutdown();
            }
            //se c'è qualche errore
            public void onError(Throwable throwable){
                System.err.println("ERROR: "+ throwable);

            }

        });


        //da qui in poi si tratta di una mini interfaccia per continuare a chiedere se si vuole sommare e cosa
        String message;
        while(true){
            System.out.println("clicca un qualsiasi carattere per continuare, altrimenti scrivi \"quit\" ");

            message = br.readLine();
            if (message.equals("quit")) {
                serverStream.onCompleted();
                break;
            }
            System.out.println("inserisci il primo numero che desideri sommare (intero plz)");
            int first = Integer.parseInt(br.readLine());
            System.out.println("inserisci il secondo numero che desideri sommare (intero plz)");
            int second =Integer.parseInt(br.readLine());

            serverStream.onNext(Request.newBuilder().setFirst(first).setSecond(second).build());
        }

        System.out.println("GoodBye!");

    }






    public static void main(String[] args) throws IOException {
        Client c = new Client();
        System.out.println("Synch simpleSum(5,9): ");

        c.synchCallSimpleSum(5,9);


        System.out.println("Asynch repeatedSum(5,10): ");
        try {
            c.asynchCallRepeatedSum(5,10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Asynch servizio bidirezionale di input -> output.");
        c.asynchCallStreamSum();

    }



}
