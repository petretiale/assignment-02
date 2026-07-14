package virtual_thread;

import common.Accumulator;
import virtual_thread.ui.GUI;

public class VtController implements VTScanListener {

    private final GUI view;
    private final VirtualThreadFSStatLib model;

    public VtController(GUI view, VirtualThreadFSStatLib statLib) {
        this.view = view;
        this.model = statLib;
    }

    public void startScan(String path, long maxFS, int nb) {
        view.setScanRunningState(true);
        view.updateStatusArea("Scanning process initialized using Virtual Threads..\n");
        model.getFSReport(path, maxFS, nb, this);
    }

    public void stopScan() {
        view.updateStatusArea("\n[!] Cancellation request dispatched to Virtual Threads.\n");
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
