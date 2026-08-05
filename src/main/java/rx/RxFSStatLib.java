package rx;

import common.Accumulator;
import io.reactivex.rxjava3.core.Single;

public interface RxFSStatLib {

    Single<Accumulator> getFSReport(String directoryPath, long maxFS, int nb);
}
