FROM openjdk:11-jre

RUN java -version

RUN mkdir /gradle-bot

COPY app/build/install/app /gradle-bot

ENV GITHUB_ACCESS_TOKEN="" \
     TEAMCITY_ACCESS_TOKEN=""

WORKDIR /gradle-bot

EXPOSE 8080

CMD ["bin/app"]