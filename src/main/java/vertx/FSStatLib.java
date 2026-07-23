package vertx;

import io.vertx.core.Future;
import common.Accumulator;

public interface FSStatLib {
    void getFSReport(String directoryPath, long maxFS, int nb, VertxScanListener listener);
}
