#!/bin/bash

# Mise à jour des paquets
sudo apt-get update

# Installation de Nginx
sudo apt-get install -y nginx

# Configuration de Nginx pour agir en tant que proxy pour l'application Spring Boot
sudo tee /etc/nginx/sites-available/myapp <<EOF
server {
    listen 80;

    server_name myapp.local;

    location / {
        proxy_pass http://192.168.33.11:8080;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF

# Activation de la configuration Nginx
sudo ln -s /etc/nginx/sites-available/myapp /etc/nginx/sites-enabled/

# Redémarrage de Nginx
sudo systemctl restart nginx