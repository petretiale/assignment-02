import io.vertx.core.CompositeFuture;
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
    public Future<Accumulator> getFSReport(String directoryPath, long maxFS, int nb) {
        return scanDirectory(directoryPath, maxFS, nb);
    }

    private io.vertx.core.Future<Accumulator> scanDirectory(String directoryPath, long maxFS, int nb) {
        return fs.readDir(directoryPath)
                .compose((List<String> entries) -> {
                    List<io.vertx.core.Future<Accumulator>> itemFutures = new ArrayList<>();

                    for (String entry : entries) {
                        io.vertx.core.Future<Accumulator> itemPropsFuture = fs.props(entry).compose(props -> {
                            if (props.isDirectory()) {
                                return scanDirectory(entry, maxFS, nb);
                            } else {
                                Accumulator acc = new Accumulator(maxFS, nb);
                                return io.vertx.core.Future.succeededFuture(acc.addFile(props.size()));
                            }
                        });

                        itemFutures.add(itemPropsFuture);
                    }

                    return io.vertx.core.Future.all(itemFutures).map((CompositeFuture res) -> {
                        Accumulator acc = new Accumulator(maxFS, nb);
                        return res.<Accumulator>list().stream().reduce(acc, Accumulator::add);
                    });
                });
    }
}
