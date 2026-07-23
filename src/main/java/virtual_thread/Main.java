package virtual_thread;

import ui.Controller;
import ui.swing.ScanGUI;

public class Main {


    public static void main(String[] args) {
        VirtualThreadFSStatLib scanner = new VirtualThreadFSStatLib();
        ScanGUI view = new ScanGUI();
        Controller controller = new VtController(view, scanner);
        view.setController(controller);
        view.setVisible(true);
    }
}
