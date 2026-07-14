package virtual_thread;

import virtual_thread.ui.swing.ScanGUI;

public class Main {


    public static void main(String[] args) {
        VirtualThreadFSStatLib scanner = new VirtualThreadFSStatLib();
        ScanGUI view = new ScanGUI();
        VtController controller = new VtController(view, scanner);
        view.setController(controller);
        view.setVisible(true);
    }
}
