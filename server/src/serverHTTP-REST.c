#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "network.h"

char *missingParametersHtml = "HTTP/1.1 200 OK\r\n\r\n{\r\n    error: \"Please, enter all the parameters\"\r\n}\r\n";


/**
 * Restituisce vero se la stringa str inizia con la stringa substr.
*/
bool StartsWith(const char *str, const char *substr)
{
   if(strncmp(str, substr, strlen(substr)) == 0) return 1;
   return 0;
}


/**
 * Restituisce la somma di due valori
*/
float calcolaSomma(float val1, float val2)  {
   return (val1 + val2);
}


/**
 * Dato un array di interi e la sua dimensione restituisce una stringa con l'array serializzato.
 *
 * NOTA: e' necessario de-allocare la stringa ottenuta di ritorno con free()
 *
 * @param array Array da serializzare
 * @param size Dimensione di array
 * @param outResultSize Dimensione della memoria allocata per far stare la stringa serializzata
 * @return result Stringa con il risultato
*/
char* serializeNumbersArray(int* array, int size, int* outResultSize){
    int len = 16;
    int writtenSize;
    char* result = calloc(len, sizeof(char));
    char* tmpresult = calloc(len, sizeof(char));

    for (int i = 0; i < size; i++){

        // tenta di aggiungere il valore serializzato alla stringa
        if (strlen(result) == 0){
            // se la stringa e' vuota sto leggendo il primo valore
            writtenSize = snprintf(tmpresult, len, "%d", array[i]);
        }
        else {
            // ho gia' letto altri valori: devo separare i valori precedenti da questo con una virgola
            writtenSize = snprintf(tmpresult, len, "%s,%d", result, array[i]);
        }

        // se la serializzazione fallisce vuol dire che non e' presente abbastanza memoria
        if (writtenSize >= len || writtenSize < 0){
            // raddoppia la quantita' di memoria disponibile
            len *= 2;
            result = realloc(result, len);
            tmpresult = realloc(tmpresult, len);

            // ri-esegui la serializzazione
            if (strlen(result) == 0){
                snprintf(tmpresult, len, "%d", array[i]);
            }
            else {
                snprintf(tmpresult, len, "%s,%d", result, array[i]);
            }
        }

        // copia il risultato della serializzazione da tmpresult a tmp
        strcpy(result, tmpresult);
    }

    // Tenta di aggiungere le parentesi attorno ai valori serializzati
    writtenSize = snprintf(tmpresult, len, "[%s]", result);

    if (writtenSize >= len || writtenSize < 0){
        // l'aggiunta di parentesi non ha avuto successo: aumenta la memoria disponibile
        len *= 2;
        result = realloc(result, len);
        tmpresult = realloc(tmpresult, len);
        writtenSize = snprintf(tmpresult, len, "[%s]", result);
    }

    // copia il risultato in result
    strcpy(result, tmpresult);

    // de-alloca la memoria temporanea e memorizza la lunghezza finale
    free(tmpresult);
    *outResultSize = len;
    return result;
}


/**
 * Calcola i numeri primi tra min e max (compresi) e memorizzali in out_array.
 * Il valore di ritorno e' il numero di valori che sono stati scritti in out_array.
*/
int calcolaNumeriPrimi(int min, int max, int* out_array){
    int a = 0, b = 0, i = 0, j = 0, flag = 0;

    int array_index = 0;

    // lower bound
    a = min;

    // upper bound
    b = max;

    // Traverse each number in the interval
    // with the help of for loop
    for (i = a; i <= b; i++) {
        // Skip 0 and 1 as they are
        // neither prime nor composite
        if (i == 1 || i == 0)
            continue;

        // flag variable to tell
        // if i is prime or not
        flag = 1;

        for (j = 2; j <= i / 2; ++j) {
            if (i % j == 0) {
                flag = 0;
                break;
            }
        }

        // flag = 1 means i is prime
        // and flag = 0 means i is not prime
        if (flag == 1){
            printf("%d\n", i);
            out_array[array_index] = i;
            array_index++;
        }
    }

    return array_index;
}


int main(){
    socketif_t sockfd;
    FILE* connfd;
    int res, i;
    long length=0;
    char request[MTU], url[MTU], method[10], c;

    // Ascolta sulla porta 80
    sockfd = createTCPServer(80);
    if (sockfd < 0){
        printf("[SERVER] Errore: %i\n", sockfd);
        return -1;
    }

    while(true) {
        // Attendi la connessione di un client
        connfd = acceptConnectionFD(sockfd);

        // resetta i buffer
        for (int i = 0; i < MTU; i++){
            request[i] = '\0';
            url[i] = '\0';
        }

        for (int i = 0; i < 10; i++){
            method[i] = '\0';
        }

        // Memorizza la prima riga della richiesta HTTP
        fgets(request, sizeof(request), connfd);

        if (request[0] == '\0'){
            // ignora la richiesta vuota
            fclose(connfd);
            continue;
        }

        // Memorizza il metodo della richiesta (GET, POST, ...)
        strcpy(method, strtok(request, " "));

        // Memorizza l'url richiesto
        strcpy(url, strtok(NULL, " "));

        // scorri le altre informazioni della richiesta memorizzando, se presente, il valore di Content-Length
        while(request[0] != '\r') {
            fgets(request, sizeof(request), connfd);
            if(strstr(request, "Content-Length:")!=NULL)  {
                length = atol(request+15);
            }
        }

        // Se e' stato inviato qualcosa tramite POST consuma i dati inviati dal client
        if(strcmp(method, "POST") == 0)  {
            for(i = 0; i < length; i++)  {
                c = fgetc(connfd);
            }
        }

        // routing delle richieste
        if (StartsWith(url, "/calcola-somma")){
            // E' stata chiamata la funzione per calcolare la somma
            printf("Chiamata a funzione sommatrice\n");

            char *function, *op1, *op2;
            float somma, val1, val2;

            // skeleton: decodifica (de-serializzazione) dei parametri
            function = strtok(url, "?&");
            op1 = strtok(NULL, "?&");
            op2 = strtok(NULL, "?&");
            strtok(op1, "=");

            if (op1 == NULL){
                // parametro mancante: restituisci una pagina di errore
                fprintf(connfd, missingParametersHtml);
                fclose(connfd);
                continue;
            }

            val1 = atof(strtok(NULL, "="));
            strtok(op2, "=");

            if (op2 == NULL){
                // parametro mancante: restituisci una pagina di errore
                fprintf(connfd, missingParametersHtml);
                fclose(connfd);
                continue;
            }

            val2 = atof(strtok(NULL, "="));

            // chiamata alla business logic
            somma = calcolaSomma(val1, val2);

            // skeleton: codifica (serializzazione) del risultato
            fprintf(connfd,"HTTP/1.1 200 OK\r\n\r\n{\r\n    \"somma\":%f\r\n}\r\n", somma);
        }
        else if (StartsWith(url, "/prime-number-interval")){
            // E' stata chiamata la funzione per calcolare i numeri primi
            char *function, *min, *max;
            int minVal, maxVal;

            // skeleton: decodifica (de-serializzazione) dei parametri
            function = strtok(url, "?&");
            min = strtok(NULL, "?&");
            max = strtok(NULL, "?&");
            strtok(min, "=");

            if (min == NULL){
                // parametro mancante: restituisci una pagina di errore
                fprintf(connfd, missingParametersHtml);
                fclose(connfd);
                continue;
            }

            minVal = atof(strtok(NULL, "="));
            strtok(max, "=");

            if (max == NULL){
                // parametro mancante: restituisci una pagina di errore
                fprintf(connfd, missingParametersHtml);
                fclose(connfd);
                continue;
            }

            maxVal = atof(strtok(NULL, "="));

            // chiamata alla business logic
            int* prime_values = calloc((maxVal - minVal + 1), sizeof(int));
            int number_of_prime_vals = calcolaNumeriPrimi(minVal, maxVal, prime_values);

            // serializza l'array
            int outSize;
            char* serializedPrimes = serializeNumbersArray(prime_values, number_of_prime_vals, &outSize);
            printf("[SERVER] Valori primi serializzati: %s\n", serializedPrimes);

            // invia al client il risultato
            fprintf(connfd, "HTTP/1.1 200 OK\r\n\r\n{\r\n    result: %s \r\n}\r\n", serializedPrimes);

            // de-alloca la memoria della stringa con i valori serializzati
            free(serializedPrimes);
            free(prime_values);
        }
        else{
            // funzione richiesta non conosciuta
            fprintf(connfd, "HTTP/1.1 200 OK\r\n\r\n{\r\n    error: \"Funzione non riconosciuta!\"\r\n}\r\n");
        }

        fclose(connfd);

        printf("\n\n[SERVER] sessione HTTP completata\n\n");
    }

    closeConnection(sockfd);
    return 0;
}
