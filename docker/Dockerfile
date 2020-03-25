# this file just for test now

#     FROM gradle -> build jar
#  -> FROM openjdk:jre -> minimum  container size -> rm build tmp file
#  -> CMD java -jar ...

FROM gradle:6.2.2-jre11
ENV GITHUB_TOKEN="" \
    GITHUB_WEBHOOK_SECRET="" \
    JENKINS_WORKER_IPS=""

RUN apt-get update \
    && apt-get install git \
    && git clone https://github.com/blindpirate/gradle-bot.git
WORKDIR gradle-bot
CMD ["gradlew","run"]
