package virtualThread;

import common.Accumulator;

public class mainVT {

    public static void main(String[] args) {
        String directoryPath = System.getProperty("user.home") + "/Documents/Lab_Machine_Learning";

        VTFSStatLib lib = new VirtualThreadFSStatLib();

        System.out.println("Avvio della scansione del File System con la libreria Virtual Threads...");
        long t0 = System.currentTimeMillis();

        Accumulator risultato = lib.getFSReport(directoryPath, 20000, 4);

        long t1 = System.currentTimeMillis();

        System.out.println("Tempo impiegato: " + (t1 - t0) + " ms");
        System.out.println("Risultati:");
        risultato.printStats();
    }
}
