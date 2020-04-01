import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.71"
//    https://github.com/bnorm/kotlin-power-assert/issues/12
//    id("com.bnorm.power.kotlin-power-assert") version "0.3.0"
    id("application")
}

apply(plugin = "json2Java")

extra["json2JavaSourceDir"] = rootDir.resolve("src/main/resources/json")
extra["json2JavaTargetDir"] = rootDir.resolve("src/main/java")
extra["json2JavaTargetPackage"] = "org.gradle.bot.model"

sourceSets.create("teamCityWorkaround").withConvention(KotlinSourceSet::class) {
    kotlin.srcDirs += file("src/teamCityWorkaround/kotlin")
}

configurations.create("ktlint")

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val vertxVersion = "4.0.0-milestone4"
    val jacksonVersion = "2.10.3"
    val teamCityRestClientVersion = "1.7.27"
    val guiceVersion = "4.2.3"
    val logbackVersion = "1.2.3"
    val junit5Version = "5.6.1"
    val guavaVersion = "28.2-jre"
    val mockitoJUnitVersion = "3.3.3"
    val ktlintVersion = "0.36.0"
    val mockKVersion = "1.9.3"

    implementation("io.vertx:vertx-web-client:$vertxVersion")
    implementation("io.vertx:vertx-core:$vertxVersion")
    implementation("io.vertx:vertx-web:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin:$vertxVersion")
    implementation("io.vertx:vertx-lang-kotlin-coroutines:$vertxVersion")

    implementation("com.google.inject:guice:$guiceVersion")
    implementation("javax.inject:javax.inject:1")

    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:$jacksonVersion")
    // Use logback logging
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback:logback-core:$logbackVersion")

    implementation("com.google.guava:guava:$guavaVersion")

    //junit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit5Version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit5Version")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoJUnitVersion")
    testImplementation("io.mockk:mockk:$mockKVersion")


    "teamCityWorkaroundImplementation"(platform("org.jetbrains.kotlin:kotlin-bom"))
    "teamCityWorkaroundImplementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    "teamCityWorkaroundImplementation"("com.squareup.retrofit:retrofit:1.9.0")
    "teamCityWorkaroundImplementation"("commons-codec:commons-codec:1.10")
    "teamCityWorkaroundImplementation"("com.jakewharton.retrofit:retrofit1-okhttp3-client:1.1.0")
    "teamCityWorkaroundImplementation"("org.slf4j:slf4j-api:1.7.12")

    "ktlint"("com.pinterest:ktlint:$ktlintVersion")

    implementation(sourceSets["teamCityWorkaround"].output)
    configurations["implementation"].extendsFrom(configurations["teamCityWorkaroundImplementation"])
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}


tasks.named("test", Test::class.java) {
    useJUnitPlatform()
}

tasks.named("run", JavaExec::class.java) {
    main = "org.gradle.bot.AppKt"
}

// https://github.com/bnorm/kotlin-power-assert/issues/12
//tasks.named("compileTestKotlin", KotlinCompile::class.java) {
//    // https://github.com/bnorm/kotlin-power-assert
//    kotlinOptions {
//        useIR = true
//    }
//}

application {
    mainClassName = "org.gradle.bot.AppKt"

    applicationDistribution.from(sourceSets["teamCityWorkaround"].output) {
        into("lib/teamCityWorkaround")
    }
}

val ktlintTask = tasks.register<JavaExec>("ktlint") {
    group = "verification"
    description = "Check Kotlin code style"
    classpath = configurations["ktlint"]
    main = "com.pinterest.ktlint.Main"
    args("src/main/**/*.kt", "src/test/**/*.kt")
}

tasks.named("check").configure { dependsOn(ktlintTask) }

tasks.register<JavaExec>("ktlintFormat") {
    group = "formatting"
    description = "Fix Kotlin code style deviations"
    classpath = configurations["ktlint"]
    main = "com.pinterest.ktlint.Main"
    args("-F", "src/main/**/*.kt", "src/test/**/*.kt")
}
