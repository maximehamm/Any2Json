plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.0"
    id("org.jetbrains.intellij") version "1.13.1"
}

val versions: Map<String, String> by rootProject.extra

intellij {
    version.set(versions["intellij-version"])
}

dependencies {
    // Json supports
    implementation ("org.json:json:20180813")

    // CSV supports
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation ("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.11.2")

}

tasks {
    tasks {
        withType<JavaCompile> {
            sourceCompatibility = "11"
            targetCompatibility = "11"
        }
        withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
            kotlinOptions.jvmTarget = "11"
        }
    }
    buildSearchableOptions {
        enabled = false
    }
    jar {
        archiveBaseName.set(rootProject.name + "-" + project.name)
    }
}