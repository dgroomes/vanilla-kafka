rootProject.name = "kafka-in-kafka-out"

include("app", "test-harness")

// temp
includeBuild("/Users/davidgroomes/repos/opensource/kafka") {
    dependencySubstitution {
        substitute(module("org.apache.kafka:kafka-clients")).with(project(":clients"))
    }
}
