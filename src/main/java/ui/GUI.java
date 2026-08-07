package ui;

import common.Accumulator;

public interface GUI {

    void setController(Controller controller);

    void setScanRunningState(boolean isRunning);

    void updateStatusArea(String message);

    void displayLiveStats(Accumulator currentAcc);

    public void displayFinalReport(boolean cancelled, Accumulator finalAcc);
}
