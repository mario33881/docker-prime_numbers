# DOCKER NETWORKING

In docker un network driver e' sinonimo di "tipo di rete".
 
- netshoot: immagine docker che contiene strumenti di analisi di rete per fare troubleshooting delle reti docker

## Comandi utili

- ```docker network ls```: visualizza reti di docker
- ```ip addr```: visualizza le informazioni delle interfacce di rete
- ```bridge link```: visualizza le interfacce connesse ai bridge virtuali

## Bridge driver - rete bridge di default

Connette i container all'host.

Per accedere dall'esterno nel container bisogna esporre una porta.

    ip a | grep docker0

I container a cui non si specifica a quale rete collegarsi si collegheranno alla rete bridge di default (interfaccia docker0).

I container ottengono un IP dal server DHCP di docker.

I container possono comunicare conoscendo gli indirizzi IP.

Con la rete di default:
- non vengono risolti i nomi: deve essere usato l'indirizzo IP
    > Gli IP, essendo dinamici, possono cambiare
- tutti i container di default possono comunicare insieme, quindi tra di loro non c'e' isolamento.

Le reti bridge custom permettono di risolvere questi problemi.

## Bridge driver - rete bridge custom

Creando un'altra rete bridge verra' creata una interfaccia virtuale.

E' presente un server DNS che risolve i nomi dei container in indirizzi IP.

## Host driver

Rimuove l'isolamento del container, come se le applicazioni del container fossero direttamente eseguite sull'host.

Non e' quindi necessario esporre le porte.

## macvlan e ipvlan

Per le applicazioni come DNS o DHCP che sono standard e che i client si aspettano di trovare su una certa porta, quando non e' possibile eseguire i server sull'host o in una rete docker di tipo host, e' possibile eseguire i container assegnandogli indirizzi IP diversi dall'host, come se fossero macchine fisiche connesse alla rete.

Ai container vengono assegnati indirizzi MAC virtuali.

### macvlan

Ad ogni container viene assegnato un indirizzo MAC virtuale.

    docker network create -d macvlan   \
                   --subnet <subnet>   \
                   --gateway <gateway> \
                   --ip-range <range>  \
                   -o parent=eth0      \
                   nomeretemacvlan

Siccome docker e' configurato per usare il suo server DHCP possono esserci conflitti di indirizzi IP.

L'IP range serve per evitare questo problema.

Impostare il server DHCP della rete locale per non assegnare gli indirizzi nell'IP range.

parent imposta l'interfaccia fisica da usare.

Siccome il container si comporta come una macchina fisica non occorre esporre le porte.


### ipvlan

Sono molto simili alle macvlan.

La differenza piu' importante e' che viene assegnato un unico indirizzo MAC virtuale a tutti i container invece di essere univoci.
> Utile se uno switch non e' configurato per lavorare con indirizzi MAC sulla stessa interfaccia fisica

Ci sono due modalita': layer 2 e layer 3.


## overlay

Usato quando vengono eseguiti piu' istanze di docker su host diversi con docker swarm.

## null driver

Il container e' completamente isolato dagli altri container e dall'host.
