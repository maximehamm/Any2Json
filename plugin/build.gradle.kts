plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.20"
    id("org.jetbrains.intellij") version "1.13.1"
}

val versions: Map<String, String> by rootProject.extra
val notes: String by rootProject.extra

dependencies {
    implementation(project(":common"))

    runtimeOnly(project(":extensions:community"))
    runtimeOnly(project(":extensions:java"))
    runtimeOnly(project(":extensions:kotlin"))
    runtimeOnly(project(":extensions:typescript"))
    runtimeOnly(project(":extensions:php"))
    runtimeOnly(project(":extensions:python"))
}

configurations.all {
     // This is important for PDF export
     // exclude("xml-apis", "xml-apis")
     // exclude("xml-apis", "xml-apis-ext")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set(versions["intellij-version"])

    plugins.set(listOf(
        "Kotlin",
        "org.intellij.intelliLang",
        "java",
        "JUnit",
        "PsiViewer:${versions["psiViewer"]}",
    ))
}

tasks {

    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "11"
    }

    patchPluginXml {
        sinceBuild.set("203")    // 2021.2.4
        untilBuild.set("243.*")

        changeNotes.set(notes)
    }

    buildSearchableOptions {
        enabled = false
    }

    jar {
        archiveBaseName.set(rootProject.name)
    }

    runPluginVerifier {
        ideVersions.set(
            listOf("IU-2022.3.1"))
    }

    publishPlugin {
        val t = System.getProperty("PublishToken")
        token.set(t)
    }
}