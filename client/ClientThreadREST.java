import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.*;

class ClientThreadREST
{
    public static void main(String[] args) throws InterruptedException {
        if(args.length < 3)
        {
            System.out.println("USAGE: java ClientREST tipofunzione op1 op2");
            System.exit(1);
        }

        RESTAPI service1 = new RESTAPI("192.168.1.193", "8080", args[0], args[1], args[2]);
        service1.start();
        service1.join();
    }
}

class RESTAPI extends Thread
{
    String server, service, param1, param2, port;

    public void run() {
        if(service.equals("calcola-somma"))
        {
            System.out.println("Risultato: " + calcolaSomma(Float.parseFloat(param1), Float.parseFloat(param2)));
        }
        else if(service.equals("prime-number-interval"))
        {
            System.out.println("Risultato: " + calcolaNumeriPrimi(Integer.parseInt(param1), Integer.parseInt(param2)));
        }
        else
        {
            System.out.println("Servizio non disponibile!");
        }

    }

    RESTAPI(String remoteServer, String port, String srvc, String p1, String p2)  {
        this.server = new String(remoteServer);
        this.service = new String(srvc);
        this.param1 = new String(p1);
        this.param2 = new String(p2);
        this.port = new String(port);
    }

    /**
     * Calcola in remoto la somma di val1 + val2 e restituisci il risultato.
     * @param val1 Primo valore
     * @param val2 Secondo valore
     * @return Somma tra i due valori
     */
    synchronized float calcolaSomma(float val1, float val2)  {

        URL u = null;
        float risultato=0;
        int i;

        // crea l'url completo con l'indirizzo del server, la porta, la funzione da eseguire e i parametri
        try
        {
            u = new URL("http://" + server + ":" + port + "/calcola-somma?param1=" + val1 + "&param2=" + val2);
            System.out.println("URL aperto: " + u);
        }
        catch (MalformedURLException e)
        {
            System.out.println("URL errato: " + u);
        }

        try
        {
            // apri la connessione ed esegui la richiesta
            URLConnection c = u.openConnection();
            c.connect();

            // leggi cio' che viene restituito dal server
            BufferedReader b = new BufferedReader(new InputStreamReader(c.getInputStream()));
            System.out.println("Lettura dei dati...");
            String s;
            while( (s = b.readLine()) != null ) {
                System.out.println(s);
                if((i = s.indexOf("somma"))!=-1)
                    risultato = Float.parseFloat(s.substring(i+7));
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return (float)risultato;
    }


    /**
     * Esegui il calcolo remoto dei numeri primi tra min e max.
     *
     */
    synchronized List<Integer> calcolaNumeriPrimi(int min, int max){

        URL u = null;

        // crea l'url completo con l'indirizzo del server, la porta, la funzione da eseguire e i parametri
        try
        {
            u = new URL("http://" + server + ":" + port + "/prime-number-interval?min=" + min + "&max=" + max);
            System.out.println("URL aperto: " + u);
        }
        catch (MalformedURLException e)
        {
            System.out.println("URL errato: " + u);
        }

        try
        {
            // apri la connessione ed esegui la richiesta
            URLConnection c = u.openConnection();
            c.connect();

            // leggi cio' che viene restituito dal server
            BufferedReader b = new BufferedReader(new InputStreamReader(c.getInputStream()));
            System.out.println("Lettura dei dati...");
            String s;
            String body = "";

            while( (s = b.readLine()) != null ) {
                System.out.println(s);
                body += s;
            }

            // converti il JSON in oggetto e restituisci la lista di valori calcolati
            ObjectMapper mapper = new ObjectMapper();
            Result res = mapper.readValue(body, Result.class);
            return res.result;
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return new ArrayList<>();
    }

}
