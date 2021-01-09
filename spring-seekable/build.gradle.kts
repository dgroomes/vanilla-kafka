plugins {
    java
    application
    id("org.springframework.boot") version "2.4.1" // releases: https://spring.io/projects/spring-boot#learn
}

apply(plugin = "io.spring.dependency-management")

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.7.0" // releases: https://kafka.apache.org/downloads

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    /**
     *  For some reason if I don't specify the version in the "kafka-clients" dependency, then the app fails at runtime
     *  with:
     *
     *     ClassNotFoundException: com.fasterxml.jackson.databind.JsonNode
     *
     *  Why? Shouldn't the dependency resolution cause Jackson to be included? Also, why does "kafka-clients" even need
     *  to be specified if "spring-kafka" is included? Doesn't "spring-kafka" bring in "kafka-clients" as a transitive
     *  dependency?
     */
    implementation("org.apache.kafka:kafka-clients:$kafkaClientVersion")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.kafka:spring-kafka")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
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
