buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.22.0")
    }
}

plugins {
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.12.0" apply false
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
    apply(plugin = "org.jetbrains.kotlinx.binary-compatibility-validator")
}