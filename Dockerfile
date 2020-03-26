FROM openjdk:11-jre

RUN java -version

RUN mkdir /gradle-bot

COPY build/install/gradle-bot /gradle-bot

ENV GITHUB_ACCESS_TOKEN="" \
    GITHUB_WEBHOOK_SECRET="" \
    TEAMCITY_ACCESS_TOKEN=""

WORKDIR /gradle-bot

CMD ["bin/gradle-bot"]
