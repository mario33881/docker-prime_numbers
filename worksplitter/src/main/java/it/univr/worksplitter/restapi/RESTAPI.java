package it.univr.worksplitter.restapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class RESTAPI extends Thread
{
    private final String server;
    private final String service;
    private final String param1;
    private final String param2;
    private final String port;
    private final List<Integer> result;

    public void run()   {
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

    public RESTAPI(String remoteServer, String port, String srvc, String p1, String p2, List<Integer> outResult)  {
        this.server = new String(remoteServer);
        this.service = new String(srvc);
        this.param1 = new String(p1);
        this.param2 = new String(p2);
        this.port = new String(port);
        this.result = outResult;
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
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Host", "primeserver.localhost");
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
        List<Integer> result = new LinkedList<>();
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
            System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestProperty("Host", "primeserver.localhost");
            c.connect();

            // leggi cio' che viene restituito dal server
            BufferedReader b = new BufferedReader(new InputStreamReader(c.getInputStream()));
            System.out.println("Lettura dei dati...");
            String s;

            while( (s = b.readLine()) != null ) {
                System.out.println(s);
                if(s.contains("result")){
                    String[] items = s.replace("result:", "").replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

                    for (int i = 0; i < items.length; i++) {
                        try {
                            result.add(Integer.parseInt(items[i]));
                            this.result.add(Integer.parseInt(items[i]));
                        } catch (NumberFormatException nfe) {};
                    }
                }
            }
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }

        return result;
    }
}
