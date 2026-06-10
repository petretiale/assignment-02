package vertx;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;

import java.util.ArrayList;
import java.util.List;

public class VertxFSStatLib implements FSStatLib {

    private final FileSystem fs;

    public VertxFSStatLib(Vertx vertx) {
        this.fs = vertx.fileSystem();
    }

    @Override
    public Future<Accumulator> getFSReport(String path, long maxFS, int nb) {
        Accumulator acc = new Accumulator(maxFS, nb);
        return processEntry(path, acc).map(v -> acc);
    }

    private Future<Void> processEntry(String path, Accumulator acc) {
        return fs.props(path).compose(props -> {
            if (props.isDirectory()) {
                return fs.readDir(path).compose(entries -> {
                    List<Future<Void>> futures = new ArrayList<>();
                    for (String entry : entries) {
                        futures.add(processEntry(entry, acc));
                    }
                    return Future.all(futures).mapEmpty();
                });
            } else {
                acc.addFile(props.size());
                return Future.succeededFuture();
            }
        });
    }
}