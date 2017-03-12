#!/usr/bin/env bash

# Add UTF-8 encoding support in the image
locale-gen en_US.UTF-8

chmod +x /setup/*.sh
apt-get update && apt-get install -y openjdk-8-jre-headless
