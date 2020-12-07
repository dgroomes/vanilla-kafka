val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.5.0" // releases: https://kafka.apache.org/downloads

repositories {
    mavenLocal()
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    repositories {
        mavenLocal()
        jcenter()
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
        "implementation"("org.slf4j:slf4j-simple:$slf4jVersion")
        "implementation"("org.apache.kafka:kafka-clients:$kafkaClientVersion")
    }
}
