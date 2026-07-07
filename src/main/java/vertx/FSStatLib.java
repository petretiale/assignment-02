package vertx;

import io.vertx.core.Future;
import common.Accumulator;

public interface FSStatLib {
    /**
     * Calcola asincronamente le statistiche sulle dimensioni dei file in una cartella.
     *
     * @param directoryPath Il percorso della cartella da scansionare
     * @param maxFS          La dimensione massima per le bande
     * @param nb             Il numero di bande
     * @return Un Future di Vert.x che conterrà l'vertx.Accumulator pronto
     */
    Future<Accumulator> getFSReport(String directoryPath, long maxFS, int nb);
}
