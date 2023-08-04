package it.univr.worksplitter.prime;

import it.univr.worksplitter.restapi.RESTAPI;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PrimeService {

    /**
     * Richiede ai server di calcolo, mediante diverse richieste HTTP, di trovare i numeri primi tra min e max compresi
     */
    public Result calculatePrimes(Integer min, Integer max) throws InterruptedException {

        if (min == null || max == null){
            throw new IllegalStateException("min and/or max parameters are not set");
        }

        if (min < 0 || max < 0){
            throw new IllegalStateException("min and max must be positive numbers");
        }

        if (max < min){
            throw new IllegalStateException("min must be less or equal than max");
        }

        List<Integer> result = Collections.synchronizedList(new ArrayList<>());

        int nValues = max - min + 1;

        // esegui almeno un thread. Esegui tanti thread quanto e' grande l'ordine di grandezza del numero di valori di cui verificare se sono primi
        int nThreads = Math.max(1, ((int) Math.floor(Math.log10(nValues))) + 1);
        int delta = nValues / nThreads;

        int start = min;

        List<RESTAPI> threads = new ArrayList<>();
        while (start < max){
            RESTAPI thread = new RESTAPI("prime_numbers-traefik-1", "80", "prime-number-interval",
                    String.valueOf(start),
                    String.valueOf(Math.min(start + delta, max)),
                    result
            );

            thread.start();
            threads.add(thread);

            start += delta+1;
        }

        // attendi che finiscano tutti i thread
        for (RESTAPI thread: threads){
            thread.join();
        }

        System.out.println("\n\nFINE:\n");
        System.out.println(result);
        return new Result(result);
    }
}
