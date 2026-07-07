package virtualThread;

import common.Accumulator;

import java.io.File;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class VirtualThreadFSStatLib implements VTFSStatLib{

    private static final List<Thread> allThreads = new ArrayList<>();
    private final Lock monitorLock = new ReentrantLock();

    @Override
    public Accumulator getFSReport(String directoryPath, long maxFS, int nb) {
        // Puliamo la lista per garantire che ogni chiamata alla libreria sia indipendente
        synchronized (allThreads) {
            allThreads.clear();
        }

        File rootFile = new File(directoryPath);
        if (!rootFile.exists() || !rootFile.isDirectory()) {
            throw new IllegalArgumentException("Il percorso specificato non esiste o non è una cartella valida.");
        }

        final Accumulator[] totalAcc = { new Accumulator(maxFS, nb) };

        Thread rootThread = Thread.ofVirtual().unstarted(() -> searchTask(rootFile, totalAcc));

        synchronized (allThreads) {
            allThreads.add(rootThread);
        }
        rootThread.start();

        for (int i = 0; i < allThreads.size(); i++) {
            try {
                allThreads.get(i).join();
            } catch (InterruptedException e) {
                System.err.println("Scansione interrotta: " + e.getMessage());
            }
        }
        return totalAcc[0];
    }



    private void searchTask(File file, Accumulator[] totalAcc) {
        try {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File f : listFiles) {
                    if(f.isDirectory()) {
                        Thread t = Thread.ofVirtual().unstarted(() -> searchTask(f, totalAcc));

                        synchronized (allThreads) {
                            allThreads.add(t);
                        }
                        t.start();

                    } else {
                        long size = f.length();

                        try {
                            monitorLock.lock();
                            totalAcc[0] = totalAcc[0].addFile(size);
                        } finally {
                            monitorLock.unlock();
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
