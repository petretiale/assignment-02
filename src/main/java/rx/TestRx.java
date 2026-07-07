package rx;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import common.Accumulator;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestRx {

    public static void main(String[] args) {


        String directoryPath = System.getProperty("user.home") + "/Documents/Lab_Machine_Learning";
        Accumulator initialAcc = new Accumulator(20000, 4);

        System.out.println("1_______");
        Flowable<Accumulator> pipeline = Flowable.fromCallable(() -> {
            return Files.list(Paths.get(directoryPath));
        })
        .subscribeOn(Schedulers.io())
        .flatMap(javaStream -> Flowable.fromStream(javaStream))
        .map(path -> path.toFile())
        .reduce(initialAcc, (acc, file) -> acc.addFile(file.length()))
        .toFlowable();
        
        // blockingSubscribe
        // costringe il thread main ad aspettare che Schedulers.io() abbia finito prima di chiudersi
        pipeline.blockingSubscribe(finalAcc -> {
            System.out.println("Result: ");
            finalAcc.printStats();
        }, throwable -> throwable.printStackTrace());

    }


}
