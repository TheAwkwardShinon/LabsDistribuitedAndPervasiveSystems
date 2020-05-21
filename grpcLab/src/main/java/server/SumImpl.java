package server;

import com.classes.grpc.Services;
import com.classes.grpc.Services.*;
import com.classes.grpc.SumGrpc.SumImplBase;
import io.grpc.stub.StreamObserver;

public class SumImpl extends SumImplBase { //sumImplBase è una classe generata automaticamente da grpc.

    @Override  //nota bene : il nome del metodo è diventato lowercase (la prima parola almeno)
    public void simpleSum(Services.Request req, StreamObserver<Services.Response> responseObserver){
        //costruisco il risultato
        Response response = Response.newBuilder().setResult(req.getFirst()+req.getSecond()).build();
        //lo invio sullo stream del client
        responseObserver.onNext(response);
        //fine ... --> "ehi client ho finito leggi pure "
        responseObserver.onCompleted();
    }



// dato un target ritorno target * 2 , poi target * 3 , etc... a seconda di quante ripetiz devo fare
    @Override
    public void repeatedSum(RepeatedRequest request, StreamObserver<Response> responseObserver){
        int i;
        int target = request.getNum();
        Response response;
        for(i = 0; i < request.getNumberOfRepetitions(); i++ ){
            response = Response.newBuilder().setResult(target * (i + 2)).build();
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();
    }


    //stream bidirezionale .. nota bene a sto punto non è più void la funzione.. ma ritorna uno stream di richieste!
    //nel caso di uno stream bidirezionale, devo definire le tre funzioni solite sia lato server che lato client
    @Override
    public StreamObserver<Request> streamSum(final StreamObserver<Response> response){

        //ritorno un observer.. cosi il client potrà chiamare questi tre metodi qua sotto!
        return new StreamObserver<Request>() {
            @Override
            public void onNext(Request request) {
                response.onNext(Response.newBuilder().setResult(request.getFirst()+request.getSecond()).build());
                //costruisco la risposta e chiamo la onNext definita dal client  che di base stamperà il risultato
            }

            @Override
            public void onError(Throwable throwable) {
                response.onError(throwable); // viene chiamata la onerror definita dal client client

            }

            @Override
            public void onCompleted() {
                response.onCompleted(); //viene chiamata la onCompleted definita dal client client
            }
        };
    }

}
