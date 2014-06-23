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
ENV LD_LIBRARY_PATH /lib-override
ENV BATCHERS_MASTER_IP 192.168.50.4
ENV BATCHERS_PROFILE slave
RUN /scripts/install_presentation.sh

RUN cp /etc/hosts /tmp/hosts
RUN mkdir -p -- /lib-override && cp /lib/x86_64-linux-gnu/libnss_files.so.2 /lib-override
RUN perl -pi -e 's:/etc/hosts:/tmp/hosts:g' /lib-override/libnss_files.so.2
RUN echo "export LD_LIBRARY_PATH=/lib-override" | tee /etc/profile.d/batchers.sh
RUN chmod +x /etc/profile.d/batchers.sh

RUN /scripts/start_tomcat_if_present.sh

ADD /scripts/set_master_ip.sh /etc/my_init.d/set_master_ip.sh

ENTRYPOINT ["/sbin/my_init"]
CMD ["--enable-insecure-key"]
