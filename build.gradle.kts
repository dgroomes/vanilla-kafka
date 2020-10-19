plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE" apply false // release: https://spring.io/projects/spring-boot#learn
}

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.5.0" // releases: https://kafka.apache.org/downloads
val junitJupiterVersion = "5.7.0" // releases: https://junit.org/junit5/docs/current/release-notes/index.html
val jacksonVersion = "2.11.2" // releases: https://github.com/FasterXML/jackson/wiki/Jackson-Releases

subprojects {
    apply(plugin = "java")
    apply(plugin = "application")
    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
        "implementation"("org.apache.kafka:kafka-clients:$kafkaClientVersion")

        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    }

    tasks {
        withType(Test::class.java) {
            useJUnitPlatform()

            testLogging {
                showStandardStreams = true
                exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
            }
        }
    }
}

project(":streams") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.kafkaplayground.streams.Main"
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-simple:$slf4jVersion")
        "implementation"("org.apache.kafka:kafka-streams:$kafkaClientVersion")
    }
}

var springSeekableProject = project(":spring-seekable")

configure(listOf(springSeekableProject)) {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    ext["junit-jupiter.version"] = junitJupiterVersion

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"("org.springframework.kafka:spring-kafka")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "junit")
        }
        "testImplementation"("org.springframework.kafka:spring-kafka-test")
    }
}
