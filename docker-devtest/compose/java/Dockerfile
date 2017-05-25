FROM openjdk:8-jre

ADD . /setup
RUN bash -c 'chmod +x /setup/*.sh'

ENV LANG C.UTF-8

# Debugging port
EXPOSE 4000

# Standard tomcat port
EXPOSE 8080

# AJP port
EXPOSE 8009
