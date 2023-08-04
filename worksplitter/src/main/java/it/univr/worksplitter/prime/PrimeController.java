package it.univr.worksplitter.prime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller che risponde alle richieste effettuate a /prime-number-interval
 */
@RestController
@RequestMapping(path = "prime-number-interval")
public class PrimeController {

    private final PrimeService primeService;

    @Autowired
    public PrimeController(PrimeService primeService) {
        this.primeService = primeService;
    }

    /**
     * Richiama il service per ottenere i numeri primi da restituire al client
     */
    @GetMapping
    public Result calculatePrimes(Integer min, Integer max) throws InterruptedException {
        return primeService.calculatePrimes(min, max);
    }
}
