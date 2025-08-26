plugins {
    kotlin("jvm") version (rootProject.extra["kotlinVersion"] as String)
    application
    id("com.github.johnrengelman.shadow") version "8.1.1"
}


application {
    mainClass.set("com.oneread.ServerKt")
}


dependencies {
    // Ktor server
    val ktor = "2.3.12"
    implementation("io.ktor:ktor-server-core-jvm:$ktor")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor")
    implementation("io.ktor:ktor-server-status-pages-jvm:$ktor")
    implementation("io.ktor:ktor-server-call-logging-jvm:$ktor")
    
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.6")
    
    
    // Kotlinx
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    
    
    // Database: Exposed + Hikari + drivers
    val exposed = "0.49.0"
    implementation("org.jetbrains.exposed:exposed-core:$exposed")
    implementation("org.jetbrains.exposed:exposed-dao:$exposed")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposed")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.xerial:sqlite-jdbc:3.46.0.0")
    implementation("org.postgresql:postgresql:42.7.3")
    
    
    // Google Tink (AEAD)
    implementation("com.google.crypto.tink:tink:1.13.0")
    
    
    // Tests
    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor")
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
    testImplementation("io.mockk:mockk:1.13.10")
}


// Create a runnable fat JAR
tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveBaseName.set("oneread-server")
    mergeServiceFiles()
}
