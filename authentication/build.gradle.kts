plugins {
    kotlin("jvm")
    kotlin("plugin.serialization")
    id("io.ktor.plugin")
}

group = "example.com"
version = "0.0.1"

application{
    mainClass.set("com.example.ApplicationKt")
}
dependencies {
    
    implementation(project(":shared"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}