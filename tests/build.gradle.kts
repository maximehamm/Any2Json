plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.13.1"
}

val versions: Map<String, String> by rootProject.extra

intellij {
    version.set("IU-2021.3.1")
    plugins.set(listOf(
        "java",
        "Kotlin",
        "JavaScriptLanguage",
        "org.intellij.intelliLang",
        "com.jetbrains.php:213.6461.83",
        "Pythonid:213.6461.79",
//        "PsiViewer:203-SNAPSHOT"
    ))
}

dependencies {
    testImplementation(project(":plugin"))
    testImplementation(project(":common"))

    testImplementation("org.jetbrains.kotlin:kotlin-stdlib")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }
    buildSearchableOptions {
        enabled = false
    }
    jar {
        archiveBaseName.set(rootProject.name + "-" + project.name)
    }
}