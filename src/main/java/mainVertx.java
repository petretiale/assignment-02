import io.vertx.core.Vertx;

public class mainVertx {

    public static void main(String[] args) {

        Vertx vertx = Vertx.vertx();
        FSStatLib lib = new VertxFSStatLib(vertx);
        //String path = "/Users/alessandropetreti/IdeaProjects/assignment-02";
        String path = System.getProperty("user.home") + "/Documents/Lab_Machine_Learning";

        lib.getFSReport(path, 20000, 4)
                .onSuccess(accumulator -> {
                    accumulator.printStats();
                    //vertx.close();
                }).onFailure(err -> {
                    System.out.println("An error occurred during scan: " + err.getMessage());
                    //vertx.close();
                });
    }
}
