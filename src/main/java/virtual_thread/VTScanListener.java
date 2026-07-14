package virtual_thread;

import common.Accumulator;

public interface VTScanListener {


    void onStatsUpdated(Accumulator currentAcc);

    // Called when the entire scanning process terminates
    // cancelled is true if the scan was stopped by the user, false if completed naturally
    void onScanFinished(boolean cancelled, Accumulator finalAcc);
}
