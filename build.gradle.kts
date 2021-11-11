/*
 * MIT License
 *
 * Copyright (c) 2021, Valaphee.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.palantir.git-version") version "0.12.3"
    kotlin("jvm") version "1.5.31"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
    mavenLocal()
}

group = "com.valaphee"
val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
val details = versionDetails()
version = "${details.lastTag}.${details.commitDistance}"

dependencies {
    api(libs.kryo)
    api(libs.jackson.module.afterburner)
    api(libs.jackson.module.kotlin)
    api(libs.gson)
    api(libs.guice)
    api(libs.hazelcast)
    api(libs.foundry.databind)
    api(libs.foundry.math)
    api(libs.cli)
    api(libs.classgraph)
    api(libs.netty)
    api(libs.fastutil)
    api(libs.jline)
    api(libs.nettyraknet.client)
    api(libs.nettyraknet.server)
    api(libs.log4j.core)
    api(libs.log4j.iostreams)
    api(libs.log4j.jul)
    api(libs.log4j.slf4j.impl)
    api(libs.jose4j)
    api(libs.kotlin.reflect)
    api(libs.kotlin.coroutine)
    testImplementation(libs.junit)
    api(libs.lz4)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "16"
        targetCompatibility = "16"
    }

    withType<KotlinCompile> { kotlinOptions { jvmTarget = "16" } }

    withType<Test> { useJUnitPlatform() }

}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    useGpgCmd()
    sign(publishing.publications)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom.apply {
                name.set("Tesseract")
                description.set("Experience Minecraft in a different way.")
                url.set("https://valaphee.com")
                scm {
                    connection.set("https://github.com/valaphee/tesseract.git")
                    developerConnection.set("https://github.com/valaphee/tesseract.git")
                    url.set("https://github.com/valaphee/tesseract")
                }
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://raw.githubusercontent.com/valaphee/tesseract/master/LICENSE.txt")
                    }
                }
                developers {
                    developer {
                        id.set("valaphee")
                        name.set("Valaphee")
                        email.set("iam@valaphee.com")
                        roles.add("owner")
                    }
                }
            }

            from(components["java"])
        }
    }
}
