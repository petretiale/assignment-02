import java.util.Arrays;

public class Accumulator {

    private final int totalFiles;
    private final long[] bands;
    private final long maxFS;
    private final int nb;


//    public static Accumulator of(long maxFS, int nb) {
//        return new Accumulator(new long[nb + 1], maxFS, nb, 0);
//    }

    public Accumulator(long maxFS, int nb) {
        this(new long[nb + 1], maxFS, nb, 0);
    }

    private Accumulator(long[] bands, long maxFS, int nb, int totalFiles) {
        this.bands = bands;
        this.maxFS = maxFS;
        this.nb = nb;
        this.totalFiles = totalFiles;
    }

    public Accumulator addFile(long size) {
        final var newBands = Arrays.copyOf(bands, bands.length);
        if (size >= maxFS) {
            newBands[nb]++;
            return new Accumulator(newBands, maxFS, nb, totalFiles + 1);
        } else {
            double width = (double) maxFS / nb;
            int index = (int) (size / width);

            if (index >= nb) {
                index = nb - 1;
            }
            newBands[index]++;
            return new Accumulator(newBands, maxFS, nb, totalFiles + 1);
        }
    }

    public Accumulator add(Accumulator acc) {
        final var newBands = Arrays.copyOf(bands, bands.length);
        for (int i = 0; i < bands.length; i++) {
            newBands[i] = newBands[i] + acc.bands[i];
        }
        return new Accumulator(newBands, maxFS, nb, this.totalFiles + acc.totalFiles);
    }

    public int getTotalFiles() {
        return totalFiles;
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
