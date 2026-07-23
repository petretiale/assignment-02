package vertx;

import io.vertx.core.Vertx;
import ui.Controller;
import ui.swing.ScanGUI;

public class MainVertx {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        VertxFSStatLib scanner = new VertxFSStatLib(vertx);
        ScanGUI view = new ScanGUI();
        Controller controller = new VertxController(view, scanner);
        view.setController(controller);
        view.setVisible(true);
    }
}
