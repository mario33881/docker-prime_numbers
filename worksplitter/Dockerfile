# -- Compila il progetto in un file jar

FROM maven:3.8.5-openjdk-17 AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean install

# -- Copia il jar in un nuovo ambiente
#    per ridurre la dimensione finale dell'immagine

FROM eclipse-temurin:17-jre-alpine
COPY --from=build /home/app/target/worksplitter-0.0.1-SNAPSHOT.jar /usr/local/lib/worksplitter.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/worksplitter.jar"]
