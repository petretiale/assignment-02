package virtual_thread.ui;

import common.Accumulator;
import virtual_thread.VtController;

public interface GUI {

    void setController(VtController controller);

    void setScanRunningState(boolean isRunning);

    void updateStatusArea(String message);

    void displayLiveStats(Accumulator currentAcc);

    public void displayFinalReport(boolean cancelled, Accumulator finalAcc);
}
