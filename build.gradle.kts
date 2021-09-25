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
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("com.palantir.git-version") version "0.12.3"
    id("edu.sc.seis.launch4j") version "2.5.0"
    kotlin("jvm") version "1.5.30"
    `maven-publish`
    signing
}

repositories {
    mavenCentral()
    maven("https://repo.codemc.org/repository/maven-public")
}

group = "com.valaphee"
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion()

dependencies {
    implementation("com.esotericsoftware:kryo:5.2.0")
    implementation("com.fasterxml.jackson.module:jackson-module-afterburner:2.12.5")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("com.google.code.gson:gson:2.8.8")
    implementation("com.google.inject:guice:5.0.1")
    implementation("com.google.inject.extensions:guice-assistedinject:5.0.1")
    implementation("com.hazelcast:hazelcast-all:4.2.2")
    implementation("com.valaphee:foundry-databind:1.3.0.0")
    implementation("com.valaphee:foundry-math:1.3.0.0")
    implementation("commons-cli:commons-cli:1.4")
    implementation("io.github.classgraph:classgraph:4.8.114")
    implementation("io.netty:netty-buffer:4.1.67.Final")
    implementation("io.netty:netty-transport-native-epoll:4.1.67.Final:linux-x86_64")
    implementation("io.netty:netty-transport-native-kqueue:4.1.67.Final:osx-x86_64")
    implementation("io.netty:netty-codec-http2:4.1.67.Final")
    implementation("it.unimi.dsi:fastutil:8.5.4")
    implementation("jline:jline:2.14.6")
    implementation("network.ycc:netty-raknet-client:0.8-SNAPSHOT")
    implementation("network.ycc:netty-raknet-server:0.8-SNAPSHOT")
    implementation("org.apache.logging.log4j:log4j-core:2.14.1")
    implementation("org.apache.logging.log4j:log4j-iostreams:2.14.1")
    implementation("org.apache.logging.log4j:log4j-jul:2.14.1")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.1")
    implementation("org.bitbucket.b_c:jose4j:0.7.9")
    implementation("org.fusesource.leveldbjni:leveldbjni-all:1.8")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.30")
    implementation("org.jetbrains.kotlinx:kotlinx-collections-immutable-jvm:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.5.2-native-mt")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.0-M1")
    implementation("org.lz4:lz4-java:1.8.0")
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

    shadowJar {
        archiveName = "tesseract.jar"

        manifest { attributes["Main-Class"] = "com.valaphee.tesseract.MainKt" }
    }
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

launch4j {
    mainClassName = "com.valaphee.tesseract.MainKt"
    headerType = "console"
    jarTask = tasks.shadowJar.get()
    icon = "${projectDir}/app.ico"
    copyright = "Copyright (c) 2021, Valaphee"
    companyName = "Valaphee"
    fileDescription = "Experience Minecraft in a different way."
    productName = "Tesseract"
    jvmOptions = setOf("--add-opens java.base/jdk.internal.misc=ALL-UNNAMED", "--add-opens=java.base/java.nio=ALL-UNNAMED", "-Dio.netty.tryReflectionSetAccessible=true")
    copyConfigurable = emptyArray<Any>()
}
