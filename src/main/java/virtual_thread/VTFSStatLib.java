package virtual_thread;

import common.Accumulator;

public interface VTFSStatLib {

    Accumulator getFSReport(String directoryPath, long maxFS, int nb);
}
