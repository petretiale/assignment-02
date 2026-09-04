package common;

import java.util.Arrays;

public class Accumulator {

    private final int totalFiles;
    private final long[] bands;
    private final long maxFS;
    private final int nb;


//    public static vertx.Accumulator of(long maxFS, int nb) {
//        return new vertx.Accumulator(new long[nb + 1], maxFS, nb, 0);
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

    public String getFormattedReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("===========================================\n");
        sb.append("      REPORT STATISTICHE FILE SYSTEM       \n");
        sb.append("===========================================\n");
        sb.append(" Totale file analizzati: ").append(totalFiles).append("\n\n");
        sb.append(" Distribuzione dimensioni:\n");

        double bandSize = (double) maxFS / nb;
        for (int i = 0; i < nb; i++) {
            long min = Math.round(i * bandSize);
            long max = Math.round((i + 1) * bandSize) - 1;
            sb.append(String.format("  [%d - %d B]: \t%d file\n", min, max, bands[i]));
        }
        sb.append(String.format("  [>= %d B]: \t\t%d file\n", maxFS, bands[nb]));
        sb.append("===========================================\n");

        return sb.toString();
    }

    public void printStats() {
        System.out.println(getFormattedReport());
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public long[] getBands() {
        return bands;
    }

    public long getMaxFS() {
        return maxFS;
    }

    public int getNb() {
        return nb;
    }
}
