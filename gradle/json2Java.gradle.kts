apply(plugin = "json2Java")

extra["json2JavaSourceDir"] = projectDir.resolve("src/main/resources/json")
extra["json2JavaTargetDir"] = projectDir.resolve("src/main/java")
extra["json2JavaTargetPackage"] = "org.gradle.bot.model"