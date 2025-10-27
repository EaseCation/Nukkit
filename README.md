![nukkit](https://github.com/Nukkit/Nukkit/blob/master/images/banner.png)

[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](LICENSE)
[![Build Status](https://ci.potestas.xyz/job/NukkitX/job/master/badge/icon)](https://ci.potestas.xyz/job/NukkitX/job/master/)
![Tests](https://img.shields.io/jenkins/t/https/ci.nukkitx.com/job/NukkitX/job/master.svg)
[![Discord](https://img.shields.io/discord/393465748535640064.svg)](https://discord.gg/5PzMkyK)

Introduction
-------------

Nukkit is nuclear-powered server software for Minecraft: Pocket Edition.
It has a few key advantages over other server software:

* Written in Java, Nukkit is faster and more stable.
* Having a friendly structure, it's easy to contribute to Nukkit's development and rewrite plugins from other platforms into Nukkit plugins.

Nukkit is **under improvement** yet, we welcome contributions. 

Links
--------------------

* __[News](https://nukkitx.com)__
* __[Forums](https://nukkitx.com/forums)__
* __[Download](https://ci.nukkitx.com/job/NukkitX/job/master)__
* __[Plugins](https://nukkitx.com/resources)__
* __[Wiki](https://nukkitx.com/wiki)__

*Thank you for visiting our official sites. Our official websites are provided free of charge, and we do not like to place ads on the home page affecting your reading. If you like this project, please [donate to us](https://nukkitx.com/donate). All the donations will only be used for Nukkit websites and services.*

Maven/Gradle Dependency (JitPack)
-------------

This EaseCation fork of Nukkit is available via JitPack. Add it to your project:

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Use specific version (via git tag)
    implementation("com.github.EaseCation:Nukkit:v1.0.0")

    // Or use latest commit from master
    implementation("com.github.EaseCation:Nukkit:master-SNAPSHOT")

    // Or use specific commit
    implementation("com.github.EaseCation:Nukkit:4ae10764")
}
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.EaseCation:Nukkit:v1.0.0'
}
```

### Maven

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.EaseCation</groupId>
        <artifactId>Nukkit</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```

**Note**: Replace `v1.0.0` with the desired version tag. Check [releases](https://github.com/EaseCation/Nukkit/releases) for available versions.

Build JAR file
-------------

First, clone this project.
Then please also clone [EaseCation/Network](git@github.com:EaseCation/Network.git) next to this project as the graph following:

```plaintext
root
├── Nukkit
└── Network
```

For Gradle installation, please refer to this guide: [Installation - Gradle](https://gradle.org/install/).

Finally, you can run:

```shell
gradle shadowJar
```

The fat jar will be generated at `target/libs/nukkit-1.0.0-all.jar`

Running
-------------
Simply run `java -jar nukkit.jar`.

Plugin API
-------------
Information on Nukkit's API can be found at the [wiki](https://nukkitx.com/wiki/nukkit/).

Contributing
------------
Please read the [CONTRIBUTING](.github/CONTRIBUTING.md) guide before submitting any issue. Issues with insufficient information or in the wrong format will be closed and will not be reviewed.
