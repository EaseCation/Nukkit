plugins {
    application
    id("ecbuild.java-conventions")
    id("ecbuild.copy-conventions")
}

extra.set("copyTo", listOf("{server}", "{login}", "{server1}"))

application {
    mainClass = "cn.nukkit.Nukkit"
}

tasks.shadowJar {
    exclude("freebsd/**/*")
    exclude("aix/**/*")
    arrayOf("arm", "armv6", "armv7", "ppc64", "ppc64le", "i386", "riscv64", "s390x", "loongarch64").forEach {
        exclude("darwin/$it/**/*")
        exclude("linux/$it/**/*")
        exclude("win/$it/**/*")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

dependencies {
    api(project(":Network:raknet"))
    api(libs.apache.commons.compress)
    api(libs.zstd)
    api(libs.apache.commons.lang3)
    api(libs.commons.io)
    api(libs.fastutil)
    api(libs.gson)
    api(libs.guava)
    api(libs.jackson)
    api(libs.jackson.datatype.guava)
    api(libs.jackson.datatype.jdk8)
    api(libs.jline.reader)
    api(libs.jline.terminal)
    api(libs.jline.terminal.jna)
    api(libs.jopt)
    api(libs.jwt)
    api(libs.leveldb.mcpe.jni)
    api(libs.leveldb.natives)
    api(libs.lmax.disruptor)
    api(libs.log4j.api)
    api(libs.log4j.core)
    api(libs.log4j.slf4j2)
    api(libs.minecrell.console)
    api(libs.org.cloudburstmc.upnp)
    api(libs.slf4j.api)
    api(libs.snakeyaml)
    api(libs.snakeyaml.engine)
    api(libs.snappy)
    api(libs.zero.allocation.hashing)
    testImplementation(libs.jupiter.api)
    testImplementation(libs.jupiter.engine)
    testAnnotationProcessor(libs.lombok)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.log4j.core)
    compileOnly(libs.lombok)
    compileOnly(libs.spotbugs.annotations)
    compileOnly(libs.javax.annotations)
}

group = "cn.nukkit"
description = "Nukkit"
