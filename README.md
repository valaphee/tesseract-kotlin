# tesseract

![license](https://img.shields.io/badge/license-MIT%20License-blue.svg)
![version](https://img.shields.io/badge/version-0.0.1-darkred.svg)
[![build](https://github.com/valaphee/tesseract/actions/workflows/build.yml/badge.svg)](https://github.com/valaphee/tesseract/actions/workflows/build.yml)

Experience Minecraft in a different way. Tesseract is a server software for Minecraft: Bedrock Edition.<br>
But it uses a different concept then most servers out there, some are for example:
- uses an **Entity-Component-System** in combination with **Dependency Injection**<br>
  which gives the software the advantage to be fully modular and therefore easy customizable with no<br>
  monolithic classes
- **Hijack Generator**: It's clear that this software won't be able to fully replicate a vanilla minecraft server<br>
  but there is a Terrain Generator which "hijacks" the vanilla one by connecting to an internal vanilla server<br>
  and capturing its chunks
- completely written in **Kotlin**, Kotlin is a modern programming language, which is JVM compatible and has<br>
  a lot of benefits compared to Java, some are for example: Null-safety, type-interference, coroutines,<br>
  higher-order functions, etc. Which all are powerful tools to write more complex software with lesser code (and failures to make)
- **Multi-threaded**, but cycles(ticks) are not run asynchronously. This means cycles help to synchronize everything again, but chunks,<br>
  entities, etc. can be processed asynchronously, but have to wait for all to finish for the current tick. But overall<br>
  it can easily benefit from systems with 32 or even more cores.

## How to build
1. `git clone https://github.com/valaphee/tesseract`
2. `cd tesseract`
3. `./gradlew build`
