plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.13.1"
}

val versions: Map<String, String> by rootProject.extra

dependencies {
    implementation(project(":common"))

    // Json
    implementation("org.json:json:20180813")

    // CSV supports
    implementation("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-csv:2.11.2")

    // YAML supports
    implementation("org.yaml:snakeyaml:1.27")
}

intellij {
    version.set(versions["intellij-version"])

//    plugins.set(listOf(
//        "java",
//        "org.intellij.scala:${versions["scala"]}",
//        "Gherkin:${versions["gherkin"]}",
//    ))
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }
    buildSearchableOptions {
        enabled = false
    }
    jar {
        archiveBaseName.set(rootProject.name + "-" + project.name)
    }
}