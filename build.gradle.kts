plugins {
    // Apply to subprojects in a convention block below
    id("com.diffplug.spotless") version "6.25.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
}


val kotlinVersion = System.getenv("KOTLIN_VERSION") ?: "2.0.0"


subprojects {
    repositories {
        mavenCentral()
    }
    
    
    // Apply Kotlin JVM to all modules
    plugins.apply("org.jetbrains.kotlin.jvm")
    
    
    extensions.configure(org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension::class.java) {
        jvmToolchain(21)
    }
    
    
    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}


spotless {
    kotlin {
        ktlint("1.2.1")
        target("**/*.kt")
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        ktlint("1.2.1")
        target("**/*.kts")
    }
}


detekt {
    buildUponDefaultConfig = true
    config.setFrom(files(".detekt.yml"))
}


// Make Kotlin plugin version available to subprojects
gradle.beforeProject {
    project.extensions.extraProperties.set("kotlinVersion", kotlinVersion)
}
