package virtual_thread;

public interface VTFSStatLib {

    void getFSReport(String directoryPath, long maxFS, int nb, VTScanListener listener);
}
