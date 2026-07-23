package vertx;

import common.Accumulator;

public interface VertxScanListener {
    void onStatsUpdated(Accumulator currentAcc);
    void onScanFinished(boolean cancelled, Accumulator finalAcc);
}
