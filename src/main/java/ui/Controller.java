package ui;

public interface Controller {
    void startScan(String path, long maxFS, int nb);
    void stopScan();
}