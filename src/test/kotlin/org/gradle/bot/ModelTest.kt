package org.gradle.bot

/*
2020-03-27 09:59:01.933 [vert.x-eventloop-thread-1] ERROR org.gradle.bot.client.GitHubClient -
com.fasterxml.jackson.databind.exc.ValueInstantiationException: Cannot construct instance of `org.gradle.bot.model.WhoAmI$Data$Viewer`, problem: Parameter specified as non-null is null: method org.gradle.bot.model.WhoAmI$Data$Viewer.<init>, parameter name
 at [Source: (String)"{"data":{"viewer":{"login":"bot-gradle","name":null}}}"; line: 1, column: 52] (through reference chain: org.gradle.bot.model.WhoAmI["data"]->org.gradle.bot.model.WhoAmI$Data["viewer"])
 */