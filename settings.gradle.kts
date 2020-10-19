rootProject.name = "kafka-playground"

include(
        "streams",
        "spring-seekable")
includeBuild("interactive")
includeBuild("spring-errors")
includeBuild("spring-headers")
