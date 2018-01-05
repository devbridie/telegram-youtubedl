import org.gradle.kotlin.dsl.kotlin

group = "com.devbridie.telegramyoutubedl"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "com.devbridie.telegramyoutubedl.MainKt"
}

plugins {
    application
    java
    kotlin("jvm").version("1.2.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib-jre8:1.2.0")
    compile("org.telegram:telegrambots:3.5")
    compile("org.telegram:telegrambotsextensions:3.5")
    compile("com.natpryce:konfig:1.6.1.0")
    compile("com.squareup.moshi:moshi:1.5.0")
    compile("com.squareup.moshi:moshi-kotlin:1.5.0")
    compile("org.apache.commons:commons-exec:1.3")
    compile("org.apache.commons:commons-lang3:3.7")
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "${project.name}-fat"
    manifest {
        attributes["Main-Class"] = "com.devbridie.telegramyoutubedl.MainKt"
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}

tasks {
    "build" {
        dependsOn(fatJar)
    }
}
