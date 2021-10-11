```
________                                           _____ 
___  __/____________________________________ ________  /_
__  /  _  _ \_  ___/_  ___/  _ \_  ___/  __ `/  ___/  __/
_  /   /  __/(__  )_(__  )/  __/  /   / /_/ // /__ / /_  
/_/    \___//____/ /____/ \___//_/    \__,_/ \___/ \__/  
```

![license](https://img.shields.io/badge/license-MIT%20License-blue.svg)
![version](https://img.shields.io/badge/version-0.0.3-darkred.svg)
[![build](https://github.com/valaphee/tesseract/actions/workflows/build.yml/badge.svg)](https://github.com/valaphee/tesseract/actions/workflows/build.yml)

Experience Minecraft in a different way. Tesseract is a server software for Minecraft: Bedrock Edition.<br>
But it uses a different concept then most servers out there, some are for example:
- **Entity-Component-System** in combination with **Dependency Injection**<br>
  which gives the software the advantage to be fully modular and therefore easy customizable with no<br>
  monolithic classes
- **Multi-threaded**, but cycles(ticks) are not run asynchronously. This means cycles help to synchronize everything again, but chunks,<br>
  entities, etc. can be processed asynchronously, but have to wait for all to finish for the current tick. But overall<br>
  it can easily benefit from systems with 32 or even more cores.
- completely written in **Kotlin**, Kotlin is a modern programming language, which is JVM compatible and has<br>
  a lot of benefits compared to Java, some are for example: Null-safety, type-interference, coroutines,<br>
  higher-order functions, etc. Which all are powerful tools to write more complex software with lesser code (and failures to make)

**Warning:** This project is currently only in use a library, because it has all packets built-in. And has other features which can be used by other server software. In its self it isn't currently an own server software.<br>
It can also be used to capture and extract data from bds offline servers, simply look in the `com.valaphee.tesseract.CapturePacketHandler` class and uncomment return statements, and then start the Main class.<br/>
Soon it will be possible to let Tesseract generate encrypted resource packs.

## Installation
1. `git clone https://github.com/valaphee/tesseract`
2. `cd tesseract`
3. `./gradlew build`
