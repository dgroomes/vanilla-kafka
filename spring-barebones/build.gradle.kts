plugins {
    java
    application
}

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val springKafkaVersion = "2.8.0" // releases: https://spring.io/projects/spring-kafka#learn

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.springframework.kafka:spring-kafka:$springKafkaVersion")

    testImplementation("org.springframework.kafka:spring-kafka-test:$springKafkaVersion")
}

application {
    mainClass.set("dgroomes.kafkaplayground.springbarebones.Main")
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
