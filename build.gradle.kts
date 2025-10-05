plugins {
    kotlin("jvm") version "2.2.0"
    id("io.ktor.plugin") version "3.0.1"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-netty:3.0.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-serialization-jackson:3.0.1")
    implementation("io.ktor:ktor-server-call-logging:3.0.1")
    implementation("io.ktor:ktor-server-auth:3.0.1")
    implementation("io.ktor:ktor-server-cors:3.0.1")
    implementation("org.jetbrains.exposed:exposed-core:0.56.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.56.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.56.0")
    implementation("com.zaxxer:HikariCP:6.0.0")
    implementation("com.mysql:mysql-connector-j:9.0.0")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-server-tests:3.0.1")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(24)
}
