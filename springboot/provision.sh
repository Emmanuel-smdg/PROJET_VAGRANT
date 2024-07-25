#!/bin/bash

# Mise à jour des paquets
sudo apt-get update

# Installation de Java et Maven
sudo apt-get install -y openjdk-8-jdk maven

# Compilation et lancement de l'application Spring Boot
cd /home/vagrant/banking_App
mvn clean package
nohup java -jar target/banking_App-0.0.1-SNAPSHOT.jar -DskipTests &