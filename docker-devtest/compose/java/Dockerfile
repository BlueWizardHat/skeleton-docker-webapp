FROM ubuntu:16.04

ADD . /setup
RUN bash -c 'source /setup/setup.sh'

ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8  

EXPOSE 4000
EXPOSE 8080
EXPOSE 8009
