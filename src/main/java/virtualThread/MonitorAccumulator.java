package virtualThread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorAccumulator {
    private final Lock lock;
    private final Accumulator accumulator;

    public MonitorAccumulator(Accumulator accumulator) {
        lock = new ReentrantLock();
        this.accumulator = accumulator;
    }

    public Accumulator getAccumulator() {
        return accumulator;
    }

    public void updateAccumulator(long size){
        try {
            lock.lock();
            accumulator.addFile(size);
        } finally {
            lock.unlock();
        }
    }

    public long getTotalFiles() {
        lock.lock();
        try {
            return accumulator.getTotalFiles();
        } finally {
            lock.unlock();
        }
    }

    public String getReportAsString() {
        lock.lock();
        try {
            return accumulator.getReportAsString();
        } finally {
            lock.unlock();
        }
    }

    public void printReport() {
        lock.lock();
        try {
            accumulator.printStats();
        } finally {
            lock.unlock();
        }
    }
}
