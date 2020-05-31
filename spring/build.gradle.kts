import org.gradle.api.tasks.testing.logging.TestExceptionFormat

// For help with the build.gradle.kts file, start with the excellent user guide in the Gradle official docs: https://docs.gradle.org/current/userguide/kotlin_dsl.html
plugins {
    java
    id("org.springframework.boot") version "2.1.6.RELEASE"
}

apply(plugin = "io.spring.dependency-management")

ext["junit-jupiter.version"] = "5.5.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "junit")
    }
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks {
    test {
        useJUnitPlatform()

        testLogging {
            showStandardStreams = true
            exceptionFormat = TestExceptionFormat.FULL
        }

        outputs.upToDateWhen { false } // never skip the tests
    }
}