package rx;

import io.reactivex.rxjava3.core.*;
import io.reactivex.rxjava3.observables.ConnectableObservable;

import java.io.IOException;


public class mainRx {

    public static void main(String[] args) throws IOException {

        RxFSStatLib lib = new RxFSStatLib();
        String path = "/Users/daniele/IdeaProjects/assignment-02";

        Observable<Accumulator> f1 = lib.getFSReport(path, 1700, 4);
        Accumulator acc = f1.blockingLast();
        acc.printStats();
//        ConnectableObservable<Accumulator> hotObservable = f1.publish();
//        hotObservable.subscribe(Accumulator::printStats);
//        hotObservable.connect();
//
//        System.out.println("Premi INVIO per terminare l'applicazione...");
//        System.in.read();
    }
}
