package rx;
import common.Accumulator;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.io.File;

public class RxFSStatLibImpl implements RxFSStatLib {

    @Override
    public Single<Accumulator> getFSReport(String directoryPath, long maxFS, int nb) {
        File rootDir = new File(directoryPath);
        if (!rootDir.exists() || !rootDir.isDirectory()) {
            return Single.error(new IllegalArgumentException("Il percorso specificato non è una directory valida."));
        }

        Accumulator initialAcc = new Accumulator(maxFS, nb);

        return getFilesStream(rootDir)
                .subscribeOn(Schedulers.io())
                .reduce(initialAcc, (acc, file) -> acc.addFile(file.length()));
    }

    private Flowable<File> getFilesStream(File directory) {
        return Flowable.fromCallable(() -> {
                    File[] files = directory.listFiles();
                    return files != null ? files : new File[0];
                })
                .flatMap(files -> Flowable.fromArray(files))
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return getFilesStream(file);
                    } else {
                        return Flowable.just(file);
                    }
                });
    }
}
