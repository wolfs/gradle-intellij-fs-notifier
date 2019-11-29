plugins {
    java
    id("org.jetbrains.intellij").version("0.4.14")
}

group = "org.gradle"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    testCompile("junit:junit:4.12")
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = "2019.2.4"
    setPlugins("gradle")
}

tasks.patchPluginXml {
    changeNotes("""
      Add change notes here.<br>
      <em>most HTML tags may be used</em>""")

}
