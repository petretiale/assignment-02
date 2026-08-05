package vertx;

import io.vertx.core.Vertx;
import common.Accumulator;

public class MainVertx {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        FSStatLib lib = new VertxFSStatLib(vertx);
        //String path = "/Users/alessandropetreti";
        String path = System.getProperty("user.home");


        lib.getFSReport(path, 20000, 4)
                .onSuccess(accumulator -> {
                    accumulator.printStats();
                    vertx.close();
                }).onFailure(err -> {
                    System.out.println("An error occurred during scan: " + err.getMessage());
                    vertx.close();
                });
    }
}