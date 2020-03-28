plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    jcenter()
}

dependencies {
    implementation("org.jsonschema2pojo:jsonschema2pojo-core:1.0.2")
    implementation(gradleApi())
}

gradlePlugin {
    plugins {
        register("json2Java") {
            id = "json2Java"
            implementationClass = "org.gradle.bot.plugin.Json2JavaPlugin"
        }
    }
}
