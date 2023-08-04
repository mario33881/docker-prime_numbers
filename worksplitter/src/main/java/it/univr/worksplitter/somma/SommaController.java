package it.univr.worksplitter.somma;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller che risponde alle richieste effettuate a /calcola-somma
 */
@RestController
@RequestMapping(path = "calcola-somma")
public class SommaController {
    private final SommaService sommaService;

    @Autowired
    public SommaController(SommaService sommaService) {
        this.sommaService = sommaService;
    }

    /**
     * Richiama il service per ottenere il risultato della somma da restituire al client
     */
    @GetMapping
    public Result calculateSum(Double op1, Double op2) throws InterruptedException {
        return sommaService.calculateSum(op1, op2);
    }
}
