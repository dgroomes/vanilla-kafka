plugins {
    java
    application
}

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaVersion = "2.8.0" // releases: https://kafka.apache.org/downloads
val jacksonVersion = "2.12.3" // releases: https://github.com/FasterXML/jackson/wiki/Jackson-Releases

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(16))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:$slf4jVersion")
    implementation("org.slf4j:slf4j-simple:$slf4jVersion")
    implementation("org.apache.kafka:kafka-clients:$kafkaVersion")
    implementation("org.apache.kafka:kafka-streams:$kafkaVersion")
    implementation(platform("com.fasterxml.jackson:jackson-bom:$jacksonVersion"))

    // jackson-module-parameter names is needed to support deserializing to Java record classes
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")
}

application {
    mainClass.set("dgroomes.kafkaplayground.streamszipcodes.Main")
}
