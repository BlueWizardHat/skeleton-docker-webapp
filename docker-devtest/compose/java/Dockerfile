FROM openjdk:8-jre

ADD . /setup
RUN bash -c 'chmod +x /setup/*.sh'

ENV LANG en_US.UTF-8  
ENV LANGUAGE en_US:en  
ENV LC_ALL en_US.UTF-8  

EXPOSE 4000
EXPOSE 8080
EXPOSE 8009
