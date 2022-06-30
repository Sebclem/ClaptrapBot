FROM openjdk:18.0.1
WORKDIR /bot_src
ARG BUILD_NBR
ARG BRANCH_NAME
ARG BRANCH_NAME
ARG GITHUB_RUN_NUMBER
ADD build/libs/ClaptrapBot-*.jar /bot_src/bot.jar
RUN java -version
CMD java -jar bot.jar
LABEL org.opencontainers.image.source=https://github.com/Sebclem/ClaptrapBot/