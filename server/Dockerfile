# -- Compila l'eseguibile

FROM alpine as build-env
RUN apk add --no-cache build-base
WORKDIR /app
COPY src/* .

RUN gcc -o serverHTTP-REST serverHTTP-REST.c network.c

# -- Memorizza l'eseguibile in un nuovo ambiente
#    per ridurre la dimensione finale dell'immagine

FROM alpine
COPY --from=build-env /app/serverHTTP-REST /app/serverHTTP-REST
WORKDIR /app
EXPOSE 80
CMD ["/app/serverHTTP-REST"]
