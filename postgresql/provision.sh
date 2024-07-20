#!/bin/bash

# Mise à jour des paquets
sudo apt-get update

# Installation de PostgreSQL
sudo apt-get install -y postgresql postgresql-contrib

# Configuration de PostgreSQL
sudo -u postgres psql -c "CREATE USER vagrant WITH PASSWORD 'vagrant';"
sudo -u postgres psql -c "CREATE DATABASE banking_app;"
sudo -u postgres psql -c "GRANT ALL PRIVILEGES ON DATABASE banking_app TO vagrant;"

# Modification des configurations de PostgreSQL pour autoriser les connexions externes
echo "listen_addresses = '*'" | sudo tee -a /etc/postgresql/10/main/postgresql.conf
echo "host    all             all             192.168.33.0/24            md5" | sudo tee -a /etc/postgresql/10/main/pg_hba.conf

# Redémarrage de PostgreSQL
sudo systemctl restart postgresql