/*
 * Copyright (c) 2021, Valaphee.
 * All rights reserved.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import proguard.gradle.ProGuardTask

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.0-beta2")
    }
}

plugins {
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.palantir.git-version") version "0.12.3"
    id("edu.sc.seis.launch4j") version "2.5.0"
    kotlin("jvm") version "1.5.30"
    `maven-publish`
    id("net.linguica.maven-settings") version "0.5"
    id("org.hibernate.build.maven-repo-auth") version "3.0.4"
    signing
}

repositories {
    mavenLocal()
    mavenCentral()
}

group = "com.valaphee"
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
version = "${details.lastTag}.${details.commitDistance}${if (details.branchName != "master") "-${details.branchName.split('/').last()}" else ""}"

dependencies {
    api("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.4")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-smile:2.12.4")
    api("com.google.code.gson:gson:2.8.8")
    api("com.google.inject:guice:5.0.1")
    api("com.valaphee:foundry-databind:1.3.0.0")
    api("com.valaphee:foundry-math:1.3.0.0")
    implementation("commons-cli:commons-cli:1.4")
    implementation("io.grpc:grpc-netty:1.40.1")
    api("io.opentelemetry:opentelemetry-api:1.5.0")
    implementation("io.opentelemetry:opentelemetry-exporter-jaeger:1.5.0")
    api("io.netty:netty-buffer:4.1.67.Final")
    implementation("io.netty:netty-transport-native-epoll:4.1.67.Final:linux-x86_64")
    implementation("io.netty:netty-transport-native-kqueue:4.1.67.Final:osx-x86_64")
    api("io.netty:netty-codec-http2:4.1.67.Final")
    api("it.unimi.dsi:fastutil:8.5.4")
    implementation("jline:jline:2.14.6")
    implementation("network.ycc:netty-raknet-server:0.8-SNAPSHOT")
    api("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-iostreams:2.14.1")
    implementation("org.apache.logging.log4j:log4j-jul:2.14.1")
    implementation("org.bitbucket.b_c:jose4j:0.7.9")
    implementation("org.fusesource.leveldbjni:leveldbjni-all:1.8")
    api("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    api("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.1-native-mt")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
    implementation("org.lz4:lz4-java:1.8.0")
    api("org.mozilla:rhino:1.7.13")
    compileOnly(kotlin("stdlib-jdk8"))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
    }

    withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "16"
            freeCompilerArgs = listOf("-Xlambdas=indy", "-Xsam-conversions=indy", "-Xopt-in=kotlin.contracts.ExperimentalContracts", "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi")
        }
    }

    withType<Test> { useJUnitPlatform() }

    task<Copy>("copyDependencies") {
        from(configurations.default)
        into("build/libs/libs")
    }

    register<ProGuardTask>("proguard") {
        dependsOn("shadowJar")
        configuration("../proguard.conf")
        injars(shadowJar.get().archivePath)
        outjars("build/libs/${project.name}.jar")
        libraryjars("C:\\Program Files\\Java\\jdk-16.0.1\\jmods")
        libraryjars(configurations.default)
    }

    shadowJar { manifest { attributes["Main-Class"] = "com.valaphee.tesseract.MainKt" } }
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing { publications { create<MavenPublication>("maven") { from(components["java"]) } } }

launch4j {
    mainClassName = "com.valaphee.tesseract.MainKt"
    headerType = "console"
    jarTask = tasks.shadowJar.get()
    icon = "${projectDir}/app.ico"
    copyright = "Copyright (c) 2021, Valaphee"
    companyName = "Valaphee"
    fileDescription = "Experience Minecraft in a different way"
    productName = "Tesseract"
    jvmOptions = setOf("--add-opens java.base/jdk.internal.misc=ALL-UNNAMED", "--add-opens=java.base/java.nio=ALL-UNNAMED", "-Dio.netty.tryReflectionSetAccessible=true", "-Dio.netty.noPreferDirect=true")
    copyConfigurable = emptyArray<Any>()
}
