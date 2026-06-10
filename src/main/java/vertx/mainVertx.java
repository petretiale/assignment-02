package vertx;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;

public class mainVertx {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        FSStatLib lib = new VertxFSStatLib(vertx);
        String path = "/Users/daniele/IdeaProjects";
        String path2 = "/Users/daniele/IdeaProjects/assignment-02/src";

        Future<Accumulator> f1 = lib.getFSReport(path, 1700, 4)
                .onSuccess(Accumulator::printStats).onFailure(err -> {
                    log("An error occurred during scan: " + err.getMessage());
                });

        log("Lanciato il primo");

        Future<Accumulator> f2 = lib.getFSReport(path2, 1700, 4)
                .onSuccess(Accumulator::printStats).onFailure(err -> {
                    log("An error occurred during scan: " + err.getMessage());
                });

        log("Lanciato il secondo");

        f1.onSuccess(r -> {
            log("Finito il primo");
        });

        f2.onSuccess(r -> {
            log("Finito il secondo");
        });

        Future.all(f1,f2).onSuccess((CompositeFuture res) -> {
            log("FINITE ENTRAMBE");
                vertx.close();
            });
    }

    static private void log(String msg) {
        System.out.println("[ " + Thread.currentThread().getName() + "  ] " + msg);
    }

}
