FROM debian:latest
ARG BUILD_NBR=nop
ARG BRANCH_NAME=nop
RUN apt-get update
RUN apt-get -y upgrade
RUN apt-get -y install openjdk-8-jre openjdk-8-jdk curl wget
WORKDIR /bot_src
ADD DownloadLast.sh /bot_src/
RUN chmod +x DownloadLast.sh
RUN ./DownloadLast.sh ${BRANCH_NAME} ${BUILD_NBR}
ENV PORT=8080
ENV TOKEN=10
CMD java -jar bot.jar -t ${TOKEN}
