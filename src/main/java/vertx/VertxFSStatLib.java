package vertx;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import common.Accumulator;

import java.util.ArrayList;
import java.util.List;

public class VertxFSStatLib implements FSStatLib {

    private final FileSystem fs;
    private boolean isCancelled = false;
    private Accumulator currentAccumulator;

    public VertxFSStatLib(Vertx vertx) {
        this.fs = vertx.fileSystem();
    }

    @Override
    public void getFSReport(String directoryPath, long maxFS, int nb, VertxScanListener listener) {
        this.isCancelled = false;
        this.currentAccumulator = new Accumulator(maxFS, nb);
        scanDirectory(directoryPath, maxFS, nb, listener)
                .onSuccess(finalAcc -> {
                    listener.onScanFinished(isCancelled, finalAcc);
                })
                .onFailure(err -> {
                    listener.onScanFinished(isCancelled, currentAccumulator);
                });
    }

    private Future<Accumulator> scanDirectory(String directoryPath, long maxFS, int nb, VertxScanListener listener) {
        if (isCancelled) {
            return Future.succeededFuture(currentAccumulator);
        }

        return fs.readDir(directoryPath)
                .compose((List<String> entries) -> {
                    List<Future<Accumulator>> itemFutures = new ArrayList<>();

                    for (String entry : entries) {
                        Future<Accumulator> itemPropsFuture = fs.props(entry)
                                .compose(props -> {
                            if (props.isDirectory()) {
                                return scanDirectory(entry, maxFS, nb, listener);
                            } else {
                                Accumulator updatedAcc;
                                this.currentAccumulator = this.currentAccumulator.addFile(props.size());
                                updatedAcc = this.currentAccumulator;

                                if (!isCancelled) {
                                    listener.onStatsUpdated(updatedAcc);
                                }
                                return Future.succeededFuture(updatedAcc);
                            }
                        });

                        itemFutures.add(itemPropsFuture);
                    }

//                    return Future.all(itemFutures).map((CompositeFuture res) -> {
//                        Accumulator acc = new Accumulator(maxFS, nb);
//                        return res.<Accumulator>list().stream().reduce(acc, Accumulator::add);
//                    });
                    return Future.join(itemFutures).map(v -> currentAccumulator);
                });
    }

    public void stopReport() {
        this.isCancelled = true;
    }
}
