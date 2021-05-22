plugins {
    java
    application
    id("org.springframework.boot") version "2.4.1" // releases: https://spring.io/projects/spring-boot#learn
}

apply(plugin = "io.spring.dependency-management")

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    /**
     * Normally, we would not need to declare a dependency on 'kafka-clients' because it is required transitively via
     * 'spring-kafka' but there is a defect in version 2.6.0 of 'kafka-clients'. See https://issues.apache.org/jira/browse/KAFKA-10384
     * So, we need to depend on a specific version to avoid 2.6.0.
     *
     * Note: when the combination of Spring Boot and Spring for Apache Kafka upgrade to a version of 'kafka-clients' newer
     * than 2.6.0 then this explicit version declaration can be deleted. In fact, please delete it!
     *
     */
    implementation("org.apache.kafka:kafka-clients") {
        version {
            strictly("2.6.1")
        }
    }
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
