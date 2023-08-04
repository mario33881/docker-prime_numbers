package it.univr.worksplitter.somma;

import org.springframework.stereotype.Service;


@Service
public class SommaService {

    /**
     * Calcola la somma tra op1 e op2
     */
    public Result calculateSum(Double op1, Double op2) {

        if (op1 == null || op2 == null){
            throw new IllegalStateException("op1 and/or op2 parameters are not set");
        }

        return new Result(op1 + op2);
    }
}
