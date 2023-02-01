plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    api(project(":jsoup-ktx"))
    api("io.ktor:ktor-client:2.2.3")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useKotlinTest()
        }
    }
}
