package virtualThread;

import java.io.File;

public class VtFSStatLib {

    private MonitorAccumulator monitor;

    public MonitorAccumulator getFSReport(String path, long maxFS, int nb) {
        monitor = new MonitorAccumulator(new Accumulator(maxFS, nb));
        Thread.ofVirtual().name("root").start(
                () -> {
                    File root = new File(path);
                    if (root.isDirectory()){
                        processEntry(root);
                    }
                }
        );
        return monitor;
    }

    public void processEntry(File dir){
        File[] files = dir.listFiles();
        if (files == null){
            return;
        }
        for (File file : files){
            if (file.isDirectory()){
                Thread.ofVirtual().name(file.getName()).start(() -> processEntry(file));
            } else {
                long fileSize = file.length();
                try {
                    monitor.updateAccumulator(fileSize);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}

