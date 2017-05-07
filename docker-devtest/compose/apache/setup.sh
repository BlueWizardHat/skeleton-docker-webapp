#!/usr/bin/env bash

echo "* Updating cache"
apt-get update

echo "* Installing locales package"
apt-get install -y locales

echo "* Installing apache and supervisor"
apt-get install -y apache2 supervisor

# Start and stop it to initialize directories
service apache2 start
sleep 10
service apache2 stop

a2enmod proxy_ajp

cp /setup/supervisord.conf /etc/supervisord.conf

# Add UTF-8 encoding support in the image
locale-gen en_US.UTF-8  
