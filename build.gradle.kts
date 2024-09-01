
val kotlin_version: String by project
val logback_version: String by project
val mongo_version: String by project
val ktor_version: String by project

plugins {
    kotlin("jvm") version "2.0.10"
    id("io.ktor.plugin") version "2.3.12"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.10"
}

group = "example.com"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

subprojects{
    apply{
        plugin("org.jetbrains.kotlin.jvm")
    }
    repositories {
        mavenCentral()
    }
    dependencies {
        implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
        implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
        implementation("ch.qos.logback:logback-classic:$logback_version")

        implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

        implementation("org.mongodb:mongodb-driver-kotlin-coroutine:$mongo_version")
        implementation("io.github.flaxoos:ktor-server-kafka-jvm:1.2.9")
        implementation("io.ktor:ktor-server-call-logging-jvm")
        implementation("io.ktor:ktor-server-host-common-jvm")
        implementation("io.ktor:ktor-server-status-pages-jvm")
        implementation("io.ktor:ktor-server-auth-jvm")
        implementation("io.ktor:ktor-server-auth-jwt-jvm")
        implementation("io.ktor:ktor-server-config-yaml")

        implementation("com.squareup.retrofit2:retrofit:2.11.0")
        implementation("com.squareup.retrofit2:converter-gson:2.11.0")


        testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    }
}
