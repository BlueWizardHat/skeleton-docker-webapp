#!/usr/bin/env bash

chmod +x /setup/*.sh
apt-get update && apt-get install -y locales openjdk-8-jre-headless

# Add UTF-8 encoding support in the image
locale-gen en_US.UTF-8
