import org.gradle.api.file.DuplicatesStrategy
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.gradle.api.publish.maven.MavenPublication

plugins {
    application
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.versions)
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://repo.opencollab.dev/maven-releases/") }
    maven { url = uri("https://repo.opencollab.dev/maven-snapshots/") }
}

group = "cn.nukkit"
description = "Nukkit"

application {
    mainClass = "cn.nukkit.Nukkit"
}

val shadowJarTask = tasks.named<ShadowJar>("shadowJar")
if (gradle.parent != null) {
    val shadow = shadowJarTask.get()
    val name = shadow.archiveBaseName.get()
    val extension = shadow.archiveExtension.get()
    val fileName = "$name.$extension"
    val output = shadow.outputs.files.singleFile
    val root = rootProject.projectDir.parentFile

    val subtasks = listOf(File(root, "_server"), File(root, "_login"), File(root, "_server1"))
        .mapIndexed { idx, dest ->
            tasks.register<Copy>("copyShadowJarSubtask$idx") {
                from(output)
                into(dest)
                rename { fileName }
                dependsOn(shadowJarTask)
            }
        }

    tasks.register<DefaultTask>("copyShadowJar") {
        group = "copy"
        subtasks.forEach { dependsOn(it) }
    }
}

tasks.shadowJar {
    transform(Log4j2PluginsCacheFileTransformer::class.java) {}
    mergeServiceFiles()
    exclude("**/module-info.class")
}

val javaLanguageVersion = JavaLanguageVersion.of(21)
the<JavaPluginExtension>().apply {
    toolchain {
        languageVersion = javaLanguageVersion
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}


publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

dependencies {
    api("com.nukkitx.network:raknet")
    api(libs.apache.commons.compress)
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
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.log4j.core)
    compileOnly(libs.lombok)
    compileOnly(libs.spotbugs.annotations)
    compileOnly(libs.javax.annotations)
}
