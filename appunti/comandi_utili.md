# Comandi utili

## docker CLI

- ```docker info```: visualizza informazioni di docker
- ```docker version```: versione di docker
- ```docker login```: permette di accedere
- ```docker pull <nome>```: permette di scaricare un'immagine da un registro
- ```docker rmi```: rimuove una immagine
- ```docker images```: visualizza una lista di tutte le immagini scaricate
- ```docker run <nome>```: esegue un container
- ```docker run <nome> --rm```: esegue un container e lo elimina a fine esecuzione
- ```docker run -it <nome> sh```: esegue un container in modo interattivo
- ```docker ps```: visualizza container in esecuzione
- ```docker ps -a```: visualizza tutti i container che sono stati eseguiti o che sono in esecuzione
- ```docker rm <ID>```: rimuove un container con un certo ID
- ```docker rm $(docker ps -a -q -f status=exited)```: rimuove tutti i container che sono terminati

    Alternativa per le ultime versioni:
    ```
    docker container prune
    ```
