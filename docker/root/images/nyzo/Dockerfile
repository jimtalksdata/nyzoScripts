FROM openjdk:8-jdk-alpine
MAINTAINER Jim (jim@jim.com)
RUN apk add --no-cache git bash wget curl nano sudo iptables
RUN sudo apk add --no-cache haveged
WORKDIR "/root"
RUN git clone https://github.com/n-y-z-o/nyzoVerifier.git
WORKDIR "/root/nyzoVerifier"
RUN ./gradlew build
RUN sudo mkdir -p /var/lib/nyzo
ADD . .
