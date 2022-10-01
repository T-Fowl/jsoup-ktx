buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.22.0")
    }
}

subprojects {
    apply(plugin = "com.vanniktech.maven.publish")
}