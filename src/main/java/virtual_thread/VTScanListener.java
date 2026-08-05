package virtual_thread;

import common.Accumulator;

public interface VTScanListener {

    void onStatsUpdated(Accumulator currentAcc);

    void onScanFinished(boolean cancelled, Accumulator finalAcc);
}
