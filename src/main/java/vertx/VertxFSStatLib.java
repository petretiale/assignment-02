package vertx;

import common.Accumulator;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.WorkerExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class VertxFSStatLib implements FSStatLib {

    private final int workerPoolSize = 8;

    private final WorkerExecutor workerExecutor;
    private boolean isCancelled = false;
    private Accumulator currentAccumulator;

    public VertxFSStatLib(Vertx vertx) {
        this.workerExecutor = vertx.createSharedWorkerExecutor("fs-scan-pool", workerPoolSize);
    }

    @Override
    public void getFSReport(String directoryPath, long maxFS, int nb, VertxScanListener listener) {
        this.isCancelled = false;
        this.currentAccumulator = new Accumulator(maxFS, nb);

        scanDirectory(directoryPath, listener)
                .onSuccess(finalAcc -> listener.onScanFinished(isCancelled, finalAcc))
                .onFailure(err -> listener.onScanFinished(isCancelled, currentAccumulator));
    }

    private Future<Accumulator> scanDirectory(String directoryPath, VertxScanListener listener) {
        if (isCancelled) {
            return Future.succeededFuture(currentAccumulator);
        }

        Future<List<String>> readDirFuture = workerExecutor.executeBlocking(() -> {
            File dir = new File(directoryPath);
            String[] names = dir.list();
            List<String> paths = new ArrayList<>();
            if (names != null) {
                for (String name : names) {
                    paths.add(new File(dir, name).getPath());
                }
            }
            return paths;
        }, false);

        return readDirFuture.compose((List<String> entries) -> {
            List<Future<Accumulator>> itemFutures = new ArrayList<>();

            for (String entry : entries) {
                if (isCancelled)  return Future.join(itemFutures).map(v -> currentAccumulator);

                Future<Accumulator> itemFuture = workerExecutor
                        .executeBlocking(() -> {
                            File f = new File(entry);
                            return f;
                        }, false)
                        .compose(f -> {
                            if (f.isDirectory()) {
                                return scanDirectory(entry, listener);
                            } else {
                                this.currentAccumulator = this.currentAccumulator.addFile(f.length());
                                if (!isCancelled) {
                                    listener.onStatsUpdated(currentAccumulator);
                                }
                                return Future.succeededFuture(currentAccumulator);
                            }
                        })
                        .recover(err -> Future.succeededFuture(currentAccumulator));

                itemFutures.add(itemFuture);
            }

            return Future.join(itemFutures).map(v -> currentAccumulator);
        });
    }

    public void stopReport() {
        this.isCancelled = true;
    }
}


//package vertx;
//import io.vertx.core.CompositeFuture;
//import io.vertx.core.Future;
//import io.vertx.core.Vertx;
//import io.vertx.core.file.FileSystem;
//import common.Accumulator;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class VertxFSStatLib implements FSStatLib {
//
//    private final FileSystem fs;
//    private boolean isCancelled = false;
//    private Accumulator currentAccumulator;
//
//    public VertxFSStatLib(Vertx vertx) {
//        this.fs = vertx.fileSystem();
//    }
//
//    @Override
//    public void getFSReport(String directoryPath, long maxFS, int nb, VertxScanListener listener) {
//        this.isCancelled = false;
//        this.currentAccumulator = new Accumulator(maxFS, nb);
//        scanDirectory(directoryPath, maxFS, nb, listener)
//                .onSuccess(finalAcc -> {
//                    listener.onScanFinished(isCancelled, finalAcc);
//                })
//                .onFailure(err -> {
//                    listener.onScanFinished(isCancelled, currentAccumulator);
//                });
//    }
//
//    private Future<Accumulator> scanDirectory(String directoryPath, long maxFS, int nb, VertxScanListener listener) {
//        if (isCancelled) {
//            return Future.succeededFuture(currentAccumulator);
//        }
//
//        return fs.readDir(directoryPath)
//                .compose((List<String> entries) -> {
//                    List<Future<Accumulator>> itemFutures = new ArrayList<>();
//
//                    for (String entry : entries) {
//                        if (isCancelled) break;
//                        Future<Accumulator> itemPropsFuture = fs.props(entry)
//                                .compose(props -> {
//                                    if (props.isDirectory()) {
//                                        return scanDirectory(entry, maxFS, nb, listener);
//                                    } else {
//                                        Accumulator updatedAcc;
//                                        this.currentAccumulator = this.currentAccumulator.addFile(props.size());
//                                        updatedAcc = this.currentAccumulator;
//
//                                        if (!isCancelled) {
//                                            listener.onStatsUpdated(updatedAcc);
//                                        }
//                                        return Future.succeededFuture(updatedAcc);
//                                    }
//                                })
//                                .recover(err -> Future.succeededFuture(currentAccumulator));
//
//                        itemFutures.add(itemPropsFuture);
//                    }
//
////                    return Future.all(itemFutures).map((CompositeFuture res) -> {
////                        Accumulator acc = new Accumulator(maxFS, nb);
////                        return res.<Accumulator>list().stream().reduce(acc, Accumulator::add);
////                    });
//                    return Future.join(itemFutures).map(v -> currentAccumulator);
//                });
//    }
//
//    public void stopReport() {
//        this.isCancelled = true;
//    }
//}