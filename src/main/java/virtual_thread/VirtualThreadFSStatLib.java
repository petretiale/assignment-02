package virtual_thread;

import common.Accumulator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VirtualThreadFSStatLib implements VTFSStatLib {

    private final Lock statsLock = new ReentrantLock();
    private final Lock threadsLock = new ReentrantLock();

    private boolean isCancelled = false;
    private Accumulator currentAccumulator;

    private final List<Thread> activeThreads = new ArrayList<>();

    @Override
    public Accumulator getFSReport(String directoryPath, long maxFS, int nb) {
        this.isCancelled = false;

        File rootFile = new File(directoryPath);
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            throw new IllegalArgumentException("Il percorso specificato non esiste o non è una cartella valida.");
        }

        this.currentAccumulator = new Accumulator(maxFS, nb);

        Thread rootThread = Thread.ofVirtual().start(() -> {
            try {
                registerThread(Thread.currentThread());
                searchTask(rootFile);
            } finally {
                unregisterThread(Thread.currentThread());
            }
        });

        try {
            rootThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        statsLock.lock();
        try {
            return this.currentAccumulator;
        } finally {
            statsLock.unlock();
        }
    }

    private void searchTask(File file) {
        if (isCancelled) return;

        try {
            File[] listFiles = file.listFiles();
            System.out.println(Thread.currentThread());
            if (listFiles != null) {
                List<Thread> subDirectoryThreads = new ArrayList<>();

                for (File f : listFiles) {
                    if (f.isDirectory()) {
                        Thread vt = Thread.ofVirtual().start(() -> {
                            registerThread(Thread.currentThread());
                            try {
                                searchTask(f);
                            } finally {
                                unregisterThread(Thread.currentThread());
                            }
                        });

                        subDirectoryThreads.add(vt);
                    } else {
                        long size = f.length();
                        Accumulator updatedAcc;

                        statsLock.lock();
                        try {
                            this.currentAccumulator = this.currentAccumulator.addFile(size);
                        } finally {
                            statsLock.unlock();
                        }
                    }
                }

                for (Thread vt : subDirectoryThreads) {
                    try {
                        vt.join();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void registerThread(Thread t) {
        threadsLock.lock();
        try {
            activeThreads.add(t);
        } finally {
            threadsLock.unlock();
        }
    }

    private void unregisterThread(Thread t) {
        threadsLock.lock();
        try {
            activeThreads.remove(t);
        } finally {
            threadsLock.unlock();
        }
    }
}