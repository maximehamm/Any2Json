@file:Suppress("PropertyName")

import java.net.URI

allprojects {
    group = "io.nimbly.json"
    version = "3.0.0"
}

val notes by extra {"""
      <b>Please Rate and Review this plugin !</b><br/><br/>
      Change notes :
      <ul>
        <li><b>2.20</b> IntelliJ IDEA 2024.3 compatibility </li>
        <li><b>2.18</b> IntelliJ IDEA 2021.2 compatibility </li>
        <li><b>2.17</b> IntelliJ IDEA 2021.2 compatibility </li>
        <li><b>2.16</b> Fix bugs </li>
        <li><b>2.15</b> Fix bugs </li>
        <li><b>2.14</b> Preview generated class sample </li>
        <li><b>2.13</b> Preview prettified Json as new editor tab </li>
        <li><b>2.12</b> Copy only selection not all file content for Xml, Csv and Properties </li>
        <li><b>2.11</b> Prettify content of .json file</li>
        <li><b>2.10</b> Convert literal from Xml, Csv, Yaml... to Json</li>
        <li><b>2.9</b> Copy Json literal inside Kotlin, Python, and Php Editor</li>
        <li><b>2.8</b> Copy Json literal inside Java Editor</li>
        <li><b>2.7</b> Prettify Json literal inside Python Editor</li>
        <li><b>2.6</b> Prettify Json literal inside PHP Editor</li>
        <li><b>2.5</b> Prettify Json literal inside Kotlin Editor</li>
        <li><b>2.4</b> Prettify Json literal inside Java Editor</li>
        <li><b>2.3</b> Supports using from Database table results</li>
        <li><b>2.2</b> Supports using from Database view</li>
        <li><b>2.1</b> Supports using from Debugger with Angular</li>
        <li><b>2.O</b> Supports of quite all IDEA products</li>
        <li><b>1.9</b> Supports of TypeScript</li>
        <li><b>1.8</b> Supports of Java Properties</li>
        <li><b>1.7</b> Supports of YAML</li>
        <li><b>1.6</b> Supports of CSV</li>
        <li><b>1.5</b> Supports of XML</li>
        <li><b>1.4</b> Supports using from debugger</li>
        <li><b>1.3</b> Mixing Kotlin and Java references</li>
        <li><b>1.2</b> Supports of Kotlin</li>
        <li><b>1.1</b> Supports of Java</li>
        <li><b>1.0</b> Initial version</li>
      </ul>
      <br/>
      Road map :
      <ul
        <li>Supports of other languages !</li>
      </ul>
      """
}

val versions by extra {
    mapOf(
        "intellij-version" to "IU-2022.3.1", //  "IU-203.7148.57",

        "python" to "223.8214.52",        // https://plugins.jetbrains.com/plugin/631-python/versions
        "php" to "223.8214.64",           // https://plugins.jetbrains.com/plugin/6610-php/versions
        "psiViewer" to "223-SNAPSHOT",    //https://plugins.jetbrains.com/plugin/227-psiviewer/versions
    )
}

allprojects {

    repositories {
        mavenCentral()
        maven {
            url = URI("https://oss.sonatype.org/content/repositories/snapshots/")
        }
        maven {
            url = URI("https://dl.bintray.com/jetbrains/intellij-plugin-service")
        }
    }
}