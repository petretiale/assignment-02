package vertx;

public class Accumulator {

    private final long[] bands;
    private final long maxFS;
    private final int nb;
    private long totalFiles = 0;

    public Accumulator(long maxFS, int nb) {
        this.bands = new long[nb + 1];
        this.maxFS = maxFS;
        this.nb = nb;
    }

    public void addFile(long size) {
        this.totalFiles++;

        if (size >= maxFS) {
            bands[nb]++;
        } else {
            double width = (double) maxFS / nb;
            int index = (int) (size / width);
            bands[Math.min(index, nb - 1)]++;
        }
    }

    public void printStats() {
        long bandWidth = maxFS / nb;

        System.out.println("\n===========================================");
        System.out.println("      REPORT STATISTICHE FILE SYSTEM       ");
        System.out.println("===========================================");
        System.out.printf(" Totale file analizzati: %d%n", totalFiles);

        for (int i = 0; i < nb; i++) {
            long low = i * bandWidth;
            long high = (i + 1) * bandWidth - 1;
            System.out.printf(" [%d - %d] byte: \t%d file%n", low, high, bands[i]);
        }

        System.out.printf(" [>= %d] byte: \t\t%d file%n", maxFS, bands[nb]);
        System.out.println("===========================================\n");
    }
}