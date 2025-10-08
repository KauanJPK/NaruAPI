plugins {
    kotlin("jvm") version "1.9.0"
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("io.ktor:ktor-server-netty:3.0.1")
    implementation("io.ktor:ktor-server-core:3.0.1")
    implementation("io.ktor:ktor-server-content-negotiation:3.0.1")
    implementation("io.ktor:ktor-serialization-jackson:3.0.1")
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("mysql:mysql-connector-java:8.0.33")
}

application {
    mainClass.set("kauanjpk.api.naruapi.MainKt")
}

kotlin {
    jvmToolchain(17)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "kauanjpk.api.naruapi.MainKt"
    }
}

tasks.shadowJar {
    archiveBaseName.set("NaruAPI")
    archiveClassifier.set("")
    archiveVersion.set("")
}
