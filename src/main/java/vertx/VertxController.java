package vertx;

import common.Accumulator;
import ui.Controller;
import ui.GUI;

public class VertxController implements Controller, VertxScanListener{

    private final GUI view;
    private final VertxFSStatLib model;

    public VertxController(GUI view, VertxFSStatLib model) {
        this.view = view;
        this.model = model;
    }

    public void startScan(String path, long maxFS, int nb) {
        view.setScanRunningState(true);
        view.updateStatusArea("Scanning process initialized using Vert.x Event-Loop..\n");
        model.getFSReport(path, maxFS, nb, this);
    }

    public void stopScan() {
        view.updateStatusArea("\n[!] Cancellation request dispatched to Vert.x Event-Loop.\n");
        model.stopReport();
    }

    @Override
    public void onStatsUpdated(Accumulator currentAcc) {
        view.displayLiveStats(currentAcc);
    }

    @Override
    public void onScanFinished(boolean cancelled, Accumulator finalAcc) {
        view.displayFinalReport(cancelled, finalAcc);
    }
}
