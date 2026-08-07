package rx;

import common.Accumulator;
import ui.Controller;
import ui.GUI;
import ui.swing.ScanGUI;

public class MainRx {

    public static void main(String[] args) {
        ScanGUI view = new ScanGUI();
        RxFSStatLib lib = new RxFSStatLibImpl();
        Controller controller = new RxController(lib, view);
        view.setController(controller);

        view.setVisible(true);
    }
}