plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "com.redwater.auto-triager"
include("core-service")
include("triager-service")
include("shared")
include("analyst")
include("organisation")
include("notification")
include("authentication")
include("api-gateway")
