package virtual_thread;

import common.Accumulator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadFSStatLib implements VTFSStatLib {

    private static final List<Thread> activeThreads = new ArrayList<>();
    private final Lock statsLock = new ReentrantLock();
    private final Lock threadsLock = new ReentrantLock();

    private boolean isCancelled = false;
    private Accumulator currentAccumulator;

    @Override
    public void getFSReport(String directoryPath, long maxFS, int nb, VTScanListener listener) {
        try {
            threadsLock.lock();
            activeThreads.clear();
        } finally {
            threadsLock.unlock();
        }

        this.isCancelled = false;

        File rootFile = new File(directoryPath);
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            throw new IllegalArgumentException("Il percorso specificato non esiste o non è una cartella valida.");
        }

        this.currentAccumulator = new Accumulator(maxFS, nb);

        Thread.ofVirtual().start(() -> {
            try {
                Thread rootThread = Thread.ofVirtual().unstarted(() -> searchTask(rootFile, listener));
                try {
                    threadsLock.lock();
                    if (!isCancelled) {
                        activeThreads.add(rootThread);
                    }
                } finally {
                    threadsLock.unlock();
                }
                rootThread.start();

                for (int i = 0; i < activeThreads.size(); i++) {
                    try {
                        activeThreads.get(i).join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("ciao" + currentAccumulator.getTotalFiles());
                listener.onScanFinished(isCancelled, this.currentAccumulator);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    public void stopReport() {
        this.isCancelled = true;
        try {
            threadsLock.lock();
            for (Thread t : activeThreads) {
                t.interrupt(); // Interrompe i thread figli attivi
            }
            activeThreads.clear(); // Svuota subito la lista per liberare memoria
        } finally {
            threadsLock.unlock();
        }
    }

    private void searchTask(File file, VTScanListener listener) {
        try {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    if (f.isDirectory()) {
                        Thread t = Thread.ofVirtual().unstarted(() -> searchTask(f, listener));

                        try {
                            threadsLock.lock();
                            if (!isCancelled) {
                                activeThreads.add(t);
                                t.start();
                            }
                        } finally {
                            threadsLock.unlock();
                        }

                    } else {
                        long size = f.length();
                        Accumulator updatedAcc;

                        try {
                            statsLock.lock();
                            this.currentAccumulator = this.currentAccumulator.addFile(size);
                            updatedAcc = this.currentAccumulator;

                        } finally {
                            statsLock.unlock();
                        }

                        if (!isCancelled) {
                            System.out.println("ciao" + currentAccumulator.getTotalFiles());
                            listener.onStatsUpdated(updatedAcc);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
