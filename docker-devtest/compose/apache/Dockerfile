FROM ubuntu:16.04

ADD . /setup
RUN bash -c 'source /setup/setup.sh'

ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8  

CMD supervisord -c /etc/supervisord.conf

EXPOSE 80
