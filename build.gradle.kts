buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.23.1")
    }
}

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.11.1" apply false
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "org.jetbrains.kotlinx.binary-compatibility-validator")
}