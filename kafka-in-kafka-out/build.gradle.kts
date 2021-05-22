val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.8.0" // releases: https://kafka.apache.org/downloads

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    repositories {
        mavenCentral()
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
        "implementation"("org.slf4j:slf4j-simple:$slf4jVersion")
        "implementation"("org.apache.kafka:kafka-clients:$kafkaClientVersion")
    }
}
