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
        return fs.props(path).compose(props -> {
            if (props.isDirectory()) {
                return fs.readDir(path).compose(entries -> {
                    List<Future<Accumulator>> futures = new ArrayList<>();
                    for (String entry : entries) {
                        futures.add(getFSReport(entry, maxFS, nb));
                    }
                    return Future.all(futures).map(compositeFuture -> {
                        Accumulator totalDir = new Accumulator(maxFS, nb);
                        for (int i = 0; i < futures.size(); i++) {
                            Accumulator childAcc = compositeFuture.resultAt(i);
                            totalDir = totalDir.add(childAcc);
                        }
                        return totalDir;
                    });
                });
            } else {
                Accumulator fileAcc = new Accumulator(maxFS, nb);
                return Future.succeededFuture(fileAcc.addFile(props.size()));
            }
        });
    }
}