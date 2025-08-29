plugins {
    kotlin("jvm") version (rootProject.extra["kotlinVersion"] as String)
    application
}


application { mainClass.set("com.oneread.ClientKt") }


dependencies {
    val ktor = "2.3.12"
    implementation("io.ktor:ktor-client-core-jvm:$ktor")
    implementation("io.ktor:ktor-client-cio-jvm:$ktor")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktor")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktor")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}
