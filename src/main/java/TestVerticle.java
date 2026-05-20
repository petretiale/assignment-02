import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.core.Vertx;
import io.vertx.core.file.FileProps;
import io.vertx.core.file.FileSystem;

import java.util.List;

class VertxFSStatLib  extends VerticleBase {

    private int count = 0;

    public Future<?> start() throws Exception {
        String path = "/Users/alessandropetreti/IdeaProjects/assignment-02";
        FileSystem fs = this.vertx.fileSystem();
        Future<List<String>> f1 = fs.readDir(path);
        f1.onComplete((AsyncResult<List<String>> res) -> {
            for (String p : res.result()) {
                count++;
                fs.props(p).onComplete((AsyncResult<FileProps> props) -> {
                    log(String.valueOf(props.result().size()));
                });
            }
        });
        log(String.valueOf(count));
        return super.start();
    }


    private static void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() + " ][ " + Thread.currentThread() + " ] " + msg);
    }
}

public class TestVerticle {

    public static void main(String[] args) {
        Vertx  vertx = Vertx.vertx();
        vertx.deployVerticle(new VertxFSStatLib())
                .onSuccess(res -> {
                    log("Reactive agent deployed.");
                });
    }


    static private void log(String msg) {
        System.out.println("[ " + System.currentTimeMillis() + " ][ " + Thread.currentThread() + " ] " + msg);
    }
}

