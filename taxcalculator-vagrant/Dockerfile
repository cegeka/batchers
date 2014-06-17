############################################################
# Dockerfile to build Cegeka batchers slave containers
# Based on phusion/baseimage
############################################################
FROM phusion/baseimage:0.9.10
MAINTAINER Andrei Petcu <Andrei.Petcu@cegeka.com>

ADD scripts scripts

RUN apt-get update

RUN apt-get install -y git software-properties-common

RUN scripts/install_oracle_java8.sh

RUN scripts/install_nodejs.sh

RUN apt-get install libxrender1 -y

RUN scripts/download_tomcat.sh

RUN scripts/install_xvfb_and_firefox.sh

RUN git clone https://github.com/cegeka/batchers.git

WORKDIR /

ENV PATH "/root/.local/bin:/root/.ndenv/bin:/root/.ndenv/shims:/root/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/lib/jvm/java-8-oracle/bin"
RUN karma --version

ENV DISPLAY :10
WORKDIR /batchers/taxcalculator
RUN /scripts/install_xvfb_and_firefox.sh && mvn clean install

RUN mkdir -p /etc/my_init.d
ADD /scripts/install_xvfb_and_firefox.sh /etc/my_init.d/install_xvfb_and_firefox.sh
ADD /scripts/start_tomcat_if_present.sh /etc/my_init.d/start_tomcat_if_present.sh

WORKDIR /
RUN /scripts/download_tomcat.sh
#RUN /scripts/install_stubwebservice.sh

EXPOSE 9091
EXPOSE 9090

#RUN /scripts/install_stubwebservice.sh
RUN /scripts/install_presentation.sh

RUN /scripts/start_tomcat_if_present.sh

CMD ["/sbin/my_init", "--enable-insecure-key"]
