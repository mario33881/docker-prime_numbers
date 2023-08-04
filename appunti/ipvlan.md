# IPVLAN

Le reti docker che usano il driver ipvlan assegnano ai container un unico indirizzo MAC in comune a tutti i container e indirizzi IP univoci della rete fisica.

Esistono due modalita' di funzionamento:
- l2: l'interfaccia fisica si comporta come uno switch.

    I messaggi di broadcast vengono inviati a tutte le sotto-interfacce virtuali dei container.

- l3: l'interfaccia fisica si comporta come un router.

    Alle sotto-interfacce virtuali devono essere assegnate sottoreti diverse.

    I messaggi di broadcast vengono bloccati.

E' consigliato usare le ipvlan al posto delle macvlan quando:
- si usa una interfaccia fisica wireless (Wi-Fi).
    > Gli access point rifiutano i frame dagli indirizzi MAC che non sono stati autenticati con l'access point stesso.
- l'interfaccia fisica ha raggiunto il limite massimo di indirizzi MAC virtuali supportati
- il traffico macvlan viene bloccato dagli switch perche' sono impostati per non accettare indirizzi MAC diversi sulla stessa interfaccia fisica

## Adattare il progetto per usare ipvlan

1. Modificare il file ```docker-compose.yml``` nella sezione ```networks```:

    - sostituire ```driver: macvlan``` con ```driver: ipvlan```
    - aggiungere l'opzione ```mode: l2``` sotto a ```driver_opts```
        > Quindi allo stesso livello di ```parent: enp0s3```

2. Per creare l'interfaccia virtuale sull'host su cui gira docker per permettere la comunicazione tra container e host bisogna eseguire i comandi:

    ```
    sudo ip link add dockervlan-shim link enp0s3 type ipvlan  mode l2
    sudo ip addr add 192.168.1.129/32 dev dockervlan-shim
    sudo ip link set dockervlan-shim up
    # usa l'interfaccia creata per comunicare con la rete contenente i container
    sudo ip route add 192.168.1.128/25 dev dockervlan-shim
    ```
    > Il type da ```macvlan``` diventa ```ipvlan``` e la mode da ```bridge``` diventa ```l2```
