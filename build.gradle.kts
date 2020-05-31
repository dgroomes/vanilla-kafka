plugins {
    id("org.springframework.boot") version "2.1.6.RELEASE" apply false
}

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

project(":interactive") {
    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.kafkaplayground.interactive.Main"
    }

    dependencies {
        "implementation"("org.slf4j:slf4j-simple:$slf4jVersion")
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

project(":spring-seekable") {
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    ext["junit-jupiter.version"] = junitJupiterVersion

    configure<ApplicationPluginConvention> {
        mainClassName = "dgroomes.kafkaplayground.springseekable.Main"
    }

    dependencies {
        "implementation"("org.springframework.boot:spring-boot-starter")
        "implementation"("org.springframework.kafka:spring-kafka")

        "testImplementation"("org.springframework.boot:spring-boot-starter-test") {
            exclude(group = "junit")
        }
        "testImplementation"("org.springframework.kafka:spring-kafka-test")
    }
}
