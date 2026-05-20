import java.util.concurrent.CompletableFuture;

public interface FSStatLib {

    CompletableFuture<Accumulator> getFSReport(String directory, long maxFS, int nb);
}
