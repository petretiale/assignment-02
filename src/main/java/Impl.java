import java.io.File;

public class Impl {

    public static void listFilesForFolder(final File folder, Accumulator acc) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry, acc);
            } else {
                acc.addFile(fileEntry.length());
            }
        }
    }

    public static void main(String[] args) {
        File folder = new File(System.getProperty("user.home") + "/Documents/Lab_Linguaggi");
        Accumulator acc = new Accumulator(1000, 4);
        listFilesForFolder(folder, acc);
        acc.printStats();
    }

}
