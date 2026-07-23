package virtual_thread;

import common.Accumulator;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadFSStatLib implements VTFSStatLib {

    private final Lock statsLock = new ReentrantLock();

    private volatile boolean isCancelled = false;
    private Accumulator currentAccumulator;
    private ExecutorService executor;

    @Override
    public void getFSReport(String directoryPath, long maxFS, int nb, VTScanListener listener) {
        this.isCancelled = false;

        File rootFile = new File(directoryPath);
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            throw new IllegalArgumentException("Il percorso specificato non esiste o non è una cartella valida.");
        }

        this.currentAccumulator = new Accumulator(maxFS, nb);
        Thread.ofVirtual().start(() -> {
            this.executor = Executors.newVirtualThreadPerTaskExecutor();
            try {
                if (!isCancelled) {
                    executor.submit(() -> {
                        searchTask(rootFile, listener);
                    });
                }
                executor.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                Accumulator finalAcc;
                statsLock.lock();
                try {
                    finalAcc = this.currentAccumulator;
                } finally {
                    statsLock.unlock();
                }
                listener.onScanFinished(isCancelled, finalAcc);
            }
        });
    }

    private void searchTask(File file, VTScanListener listener) {
        if (isCancelled) return;

        try {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    if (f.isDirectory()) {
                        searchTask(f, listener);
                    } else {
                        long size = f.length();
                        Accumulator updatedAcc;

                        statsLock.lock();
                        try {
                            this.currentAccumulator = this.currentAccumulator.addFile(size);
                            updatedAcc = this.currentAccumulator;
                        } finally {
                            statsLock.unlock();
                        }
                        if (!isCancelled) {
                            listener.onStatsUpdated(updatedAcc);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stopReport() {
        this.isCancelled = true;
        if (executor != null) {
            executor.shutdownNow();
        }
    }


}
