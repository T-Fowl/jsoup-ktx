plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.22"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api(project(":jsoup-ktx"))
    api("io.ktor:ktor-client:2.1.1")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}
