package virtual_thread;

import common.Accumulator;
import ui.Controller;
import ui.swing.ScanGUI;

public class MainVT {


    public static void main(String[] args) {

        String path = System.getProperty("user.home");
        VirtualThreadFSStatLib scanner = new VirtualThreadFSStatLib();

        Accumulator acc = scanner.getFSReport(path, 20000, 4);
        acc.printStats();
    }
}
