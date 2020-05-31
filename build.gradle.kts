val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.5.0" // releases: https://kafka.apache.org/downloads
val junitJupiterVersion = "5.6.2" // releases: https://junit.org/junit5/docs/current/release-notes/index.html

subprojects {
    apply(plugin = "java")
    apply(plugin = "application")
    repositories {
        mavenCentral()
    }

    dependencies {
        // Am I doing this right? Putting "implementation" in quotes? Can't I omit the quotes and get the strong typing?
        // Isn't strong typing and auto-complete the whole point of the Gradle Kotlin DSL versus the traditional
        // Groovy-based build.gradle file? Well, according to the official examplesl, this is the right trick! https://github.com/gradle/gradle/blob/da613276316a21508a6e30e19d1923a312578b84/subprojects/docs/src/snippets/multiproject/dependencies-java/kotlin/build.gradle.kts#L9
        //
        // This is a little disappointing... but overall the Gradle Kotlin DSL is pretty cool and works pretty well in
        // Intellij.
        "implementation"("org.slf4j:slf4j-api:$slf4jVersion")
        "implementation"("org.slf4j:slf4j-simple:$slf4jVersion")
        "implementation"("org.apache.kafka:kafka-clients:$kafkaClientVersion")

        "testImplementation"("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    }
}

project(":interactive") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.kafkaplayground.interactive.Main"
    }
}

project(":streams") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.Main"
    }

    dependencies {
        "implementation"("org.apache.kafka:kafka-streams:$kafkaClientVersion")
    }
}
