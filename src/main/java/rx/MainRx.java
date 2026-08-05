package rx;

import common.Accumulator;

public class MainRx {

    public static void main(String[] args) {
        String path = System.getProperty("user.home");
        long maxFS = 20000;
        int nb = 4;

        RxFSStatLib lib = new RxFSStatLibImpl();

        System.out.println("scansione su: " + path);

        Accumulator finalAcc = lib.getFSReport(path, maxFS, nb)
                .blockingGet();

        finalAcc.printStats();
    }
}