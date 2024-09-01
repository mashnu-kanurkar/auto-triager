
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
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(19)
}
