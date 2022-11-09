plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api(project(":jsoup-ktx"))
    api("com.squareup.okhttp3:okhttp:4.10.0")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}
