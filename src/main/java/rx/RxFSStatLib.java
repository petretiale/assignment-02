package rx;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;

public class RxFSStatLib{

    public Observable<Accumulator> getFSReport(String path, long maxFS, int nb) {
        File root = new File(path);
        Accumulator initialAcc = new Accumulator(maxFS, nb);

        return processEntry(root)
                .subscribeOn(Schedulers.io())
                .map(File::length)
                .scan(initialAcc, (acc, size) -> {
                    acc.addFile(size);
                    return acc;
                });
    }

    private Observable<File> processEntry(File dir) {
        File[] files = dir.listFiles();

        if (files == null) {
            return Observable.empty();
        }

        return Observable.fromArray(files)
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return processEntry(file);
                    } else {
                        return Observable.just(file);
                    }
                });
    }
}