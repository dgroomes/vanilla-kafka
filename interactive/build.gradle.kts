val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.5.0" // releases: https://kafka.apache.org/downloads
val junitJupiterVersion = "5.7.0" // releases: https://junit.org/junit5/docs/current/release-notes/index.html
val jacksonVersion = "2.11.2" // releases: https://github.com/FasterXML/jackson/wiki/Jackson-Releases

plugins {
    java
    application
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaClientVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
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

application {

    mainClass.set("dgroomes.kafkaplayground.interactive.Main")
}
