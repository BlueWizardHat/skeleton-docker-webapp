#!/usr/bin/env bash

echo "* Updating cache"
apt-get update

echo "* Installing apache and supervisor"
apt-get install -y apache2 supervisor

# Start and stop it to initialize directories
service apache2 start
sleep 10
service apache2 stop

a2enmod proxy_ajp

cp /setup/supervisord.conf /etc/supervisord.conf

locale-gen en_US.UTF-8  
