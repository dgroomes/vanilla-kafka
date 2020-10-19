rootProject.name = "kafka-playground"

include(
        "streams",
        "spring-seekable",
        "spring-headers")
includeBuild("interactive")
includeBuild("spring-errors")
