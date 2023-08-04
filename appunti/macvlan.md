# macvlan

```bash
docker network create -d macvlan -o parent=enp0s3 \
  --subnet 192.168.1.0/24 \
  --gateway 192.168.1.1 \
  --ip-range 192.168.1.128/25 \
  --aux-address 'host=192.168.1.129' \
  mynet
```

Crea:
- una rete di tipo macvlan (```-d```)
- sull'interfaccia dell'host ```enp0s3```
- che appartiene alla sottorete ```192.168.1.0/24```
- il cui default gateway e' ``````
- degli indirizzi appartenenti alla sottorete deve assegnare solo quelli nel range specificato da ```--ip-range```
    > Impostando il DHCP per non assegnare indirizzi in questo range non avro' rischio di conflitti.
- riserva l'indirizzo ```--aux-address``` per permettere all'host di comunicare con i container
- il nome della rete sara' ```mynet```

Se si fa cosi' l'host non sara' in grado di parlare con i container e viceversa. I container potranno comunicare fra di loro e con gli altri host della rete.
> L'indirizzo --aux-address permette di aggiungere una interfaccia macvlan sull'host per permettere la comunicazione tra host e container

Per permettere la comunicazione e' necessario creare una interfaccia macvlan sull'host:
```bash
sudo ip link add mynet-shim link enp0s3 type macvlan  mode bridge
sudo ip addr add 192.168.1.129/32 dev mynet-shim
sudo ip link set mynet-shim up
# usa l'interfaccia creata per comunicare con la rete contenente i container
sudo ip route add 192.168.1.128/25 dev mynet-shim
```
> ```mynet-shim``` e' il nome della nuova interfaccia, ```192.168.1.129/32``` permette di assegnare l'indirizzo IP che avra' la macchina nella rete macvlan

E' infine necessario attivare la modalita' promiscua:

```bash
sudo ip link set enp0s3 promisc on
```
> Se si e' su virtualbox bisogna attivare la modalita' promiscua anche dalle impostazioni di rete della macchina virtuale dopo aver impostato la rete di tipo bridge
