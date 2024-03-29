FROM openjdk:21
WORKDIR /bot_src
ARG BUILD_NBR
ARG BRANCH_NAME
ARG BRANCH_NAME
ARG GITHUB_RUN_NUMBER
ADD build/libs/ClaptrapBot.jar /bot_src/claptrapbot.jar
RUN java -version
CMD java -jar claptrapbot.jar
LABEL org.opencontainers.image.source=https://github.com/Sebclem/ClaptrapBot/