plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE" apply false // release: https://spring.io/projects/spring-boot#learn
}

val slf4jVersion = "1.7.30" // releases: http://www.slf4j.org/news.html
val kafkaClientVersion = "2.5.0" // releases: https://kafka.apache.org/downloads
val junitJupiterVersion = "5.7.0" // releases: https://junit.org/junit5/docs/current/release-notes/index.html
val jacksonVersion = "2.11.3" // releases: https://github.com/FasterXML/jackson/wiki/Jackson-Releases

subprojects {
    apply(plugin = "java")
    apply(plugin = "application")
    repositories {
        mavenCentral()
    }

    dependencies {
        /*
        Am I doing this right? Putting "implementation" in quotes? Can't I omit the quotes and get the strong typing?
        Isn't strong typing and auto-complete the whole point of the Gradle Kotlin DSL versus the traditional
        Groovy-based build.gradle file? Well, according to the official examplesl, this is the right trick! https://github.com/gradle/gradle/blob/da613276316a21508a6e30e19d1923a312578b84/subprojects/docs/src/snippets/multiproject/dependencies-java/kotlin/build.gradle.kts#L9

        This is a little disappointing... but overall the Gradle Kotlin DSL is pretty cool and works pretty well in
        Intellij.

        UPDATE 2020-05-31 Yes it looks like there is an official word on this topic. The activity of configuring a
        sub-project is defined as "cross-configuring" (https://docs.gradle.org/current/userguide/kotlin_dsl.html#sec:kotlin_cross_project_configuration)
        The note at that article says "Taking this approach means that you won’t be able to use type-safe accessors for
        model elements contributed by the plugins. You will instead have to rely on string literals and the standard
        Gradle APIs." So there you have it: cross-configuring is a significant trade-off that might might make your
        project's build files more DRY/expressive but limits the abilit of the Gradle Kotlin DSL to bring type-safety to
        those very same build files.
        */
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

var springSeekableProject = project(":spring-seekable")
var springHeadersProject = project(":spring-headers")
var springErrorsProject = project(":spring-errors")
configure(listOf(springHeadersProject, springErrorsProject)) {
    dependencies {
        // Add Jackson as a runtime dependency because it will enable the Spring Kafka "type-detection machinery" that
        // we are trying to explore in this sub-project.
        "implementation"("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    }
}

configure(listOf(springSeekableProject, springHeadersProject, springErrorsProject)) {
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
