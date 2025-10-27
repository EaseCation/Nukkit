import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import org.gradle.api.publish.maven.MavenPublication

plugins {
    application
    `java-library`
    `maven-publish`
    alias(libs.plugins.shadow)
    alias(libs.plugins.git)
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

gitProperties {
    dateFormat = "yyyy.MM.dd '@' HH:mm:ss z"
    failOnNoGitDirectory = false
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
        group = "_ec"
        subtasks.forEach { dependsOn(it) }
    }

    fun getBool(key: String, default: Boolean) =
        runCatching { project.extra.get(key) }
            .getOrNull()
            ?.let {
                if (it !is Boolean) throw GradleException("'copyToDeployTest' is not a Boolean")
                it
            }
            ?: default

    if (getBool("copyToDeployTest", default = true)) {
        tasks.register<Copy>("copyToDeployTest") {
            group = "_ec"
            from(output)
            into(File(root, "deploytest"))
            rename { fileName }
            dependsOn(shadowJarTask)
        }
    }

    if (getBool("copyToDeploy", default = true)) {
        tasks.register<Copy>("copyToDeploy") {
            group = "_ec"
            from(output)
            into(File(root, "deploy"))
            rename { fileName }
            dependsOn(shadowJarTask)
        }
    }
}

tasks.shadowJar {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest.attributes["Enable-Native-Access"] = "ALL-UNNAMED"
    manifest.attributes["Add-Opens"] = "java.base/jdk.internal.misc=ALL-UNNAMED java.base/java.nio=ALL-UNNAMED java.base/java.lang=ALL-UNNAMED"
    transform(Log4j2PluginsCacheFileTransformer::class.java) {}
    mergeServiceFiles()
    exclude("**/module-info.class")
    exclude("freebsd/**/*")
    exclude("aix/**/*")
    arrayOf("arm", "armv6", "armv7", "ppc64", "ppc64le", "i386", "riscv64", "s390x", "loongarch64").forEach {
        exclude("darwin/$it/**/*")
        exclude("linux/$it/**/*")
        exclude("win/$it/**/*")
    }
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
    // 忽略 javadoc 警告和错误，避免构建失败
    (options as StandardJavadocDocletOptions).apply {
        addStringOption("Xdoclint:none", "-quiet")
        addStringOption("encoding", "UTF-8")
        addStringOption("charSet", "UTF-8")
    }
    // 忽略 javadoc 错误
    isFailOnError = false
}

// 创建源码 JAR 任务
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
}

// 创建 Javadoc JAR 任务
tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
    dependsOn(tasks.javadoc)
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])

        // 添加源码和文档 artifact
        artifact(tasks["sourcesJar"])
        artifact(tasks["javadocJar"])

        // 配置 POM 元数据
        pom {
            name = "Nukkit"
            description = "Nuclear-powered server software for Minecraft: Bedrock Edition - EaseCation Fork"
            url = "https://github.com/EaseCation/Nukkit"

            licenses {
                license {
                    name = "GNU General Public License v3.0"
                    url = "https://www.gnu.org/licenses/gpl-3.0.html"
                }
            }

            developers {
                developer {
                    id = "easecation"
                    name = "EaseCation Team"
                    url = "https://github.com/EaseCation"
                }
            }

            scm {
                connection = "scm:git:git://github.com/EaseCation/Nukkit.git"
                developerConnection = "scm:git:ssh://github.com/EaseCation/Nukkit.git"
                url = "https://github.com/EaseCation/Nukkit"
            }
        }
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
    api(libs.jline.terminal.ffm)
    api(libs.jopt)
    api(libs.jwt)
    api(libs.leveldb.mcpe.jni)
    api(libs.leveldb.natives)
    api(libs.lmax.disruptor)
    api(libs.lmbda)
    api(libs.log4j.core)
    api(libs.log4j.slf4j2)
    api(libs.maven.provider)
    api(libs.maven.connector)
    api(libs.maven.http)
    api(libs.minecrell.console)
    api(libs.org.cloudburstmc.upnp)
    api(libs.slf4j.api)
    api(libs.snakeyaml)
    api(libs.snakeyaml.engine)
    api(libs.snappy)
    api(libs.zero.allocation.hashing)
    api(libs.zstd)
    testImplementation(libs.jupiter.engine)
    annotationProcessor(libs.lombok)
    annotationProcessor(libs.log4j.core)
    compileOnly(libs.lombok)
    compileOnly(libs.javax.annotations)
}
