public class Accumulator {
    private int totalFiles = 0;
    private final long[] bands;
    private final long maxFS;
    private final int nb;

    public Accumulator(long maxFS, int nb) {
        this.maxFS = maxFS;
        this.nb = nb;
        this.bands = new long[nb + 1];
    }

    public void addFile(long size) {
        totalFiles++;
        if (size >= maxFS) {
            bands[nb]++;
        } else {
            double width = (double) maxFS / nb;
            int index = (int) (size / width);

            if (index >= nb) {
                index = nb - 1;
            }
            bands[index]++;
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
