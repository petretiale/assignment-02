package rx;

import common.Accumulator;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface RxFSStatLib {

    Flowable<Accumulator> getFSReport(String directoryPath, long maxFS, int nb);
}
