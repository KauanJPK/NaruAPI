plugins {
    kotlin("jvm") version "2.2.0"
    application
}

group = "org.kauanjpk.naruapi"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Ktor
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-netty:3.0.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-serialization-jackson:3.0.1")

    // MySQL
    implementation("mysql:mysql-connector-java:8.0.33")

    // Dotenv
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Auth0 JWT
    implementation("com.auth0:java-jwt:4.4.0")

    // Testes
    testImplementation(kotlin("test"))
}

application {
    // Ajuste para seu package principal
    mainClass.set("kauanjpk.api.naruapi.MainKt")
}

kotlin {
    jvmToolchain(17)
}
