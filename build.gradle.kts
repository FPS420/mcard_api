val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kmongoVersion = "4.2.7"

plugins {
    application
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.4.21"

}

group = "de.mcard"
version = "0.0.1"
application {
    mainClass.set("de.mcard.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation ("io.ktor:ktor-serialization:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    implementation("org.litote.kmongo:kmongo:$kmongoVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test:$kotlin_version")
}