package rx;

import common.Accumulator;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import ui.Controller;
import ui.GUI;

import javax.swing.*;

public class RxController implements Controller {

    private final RxFSStatLib model;
    private final GUI view;

    private Disposable scanDisposable;
    private Accumulator lastAccumulator;

    public RxController(RxFSStatLib model, GUI view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void startScan(String path, long maxFS, int nb) {
        view.setScanRunningState(true);

        scanDisposable = model.getFSReport(path, maxFS, nb)
                .observeOn(Schedulers.from(SwingUtilities::invokeLater))
                .subscribe(
                        acc -> {
                            this.lastAccumulator = acc;
                            view.displayLiveStats(acc);
                        },
                        error -> {
                            view.updateStatusArea("Errore: " + error.getMessage() + "\n");
                            view.setScanRunningState(false);
                        },
                        () -> {
                            view.displayFinalReport(false, lastAccumulator);
                        }
                );
    }

    @Override
    public void stopScan() {
        if (scanDisposable != null && !scanDisposable.isDisposed()) {
            scanDisposable.dispose();
        }
        view.displayFinalReport(true, lastAccumulator);
    }
}
