rootProject.name = "nukkit"

val buildLogic = File(rootProject.projectDir.parent, "build-logic")
val network = File(rootProject.projectDir.parent, "Network")

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

if (gradle.parent != null && buildLogic.exists()) {
    includeBuild(buildLogic)
    logger.lifecycle("Nukkit Project as a composite build")
}

if (gradle.parent == null) {
    includeBuild(network) {
        dependencySubstitution {
            substitute(module("com.nukkitx.network:raknet")).using(project(":raknet"))
        }
    }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
