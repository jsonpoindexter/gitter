import org.jetbrains.kotlin.config.AnalysisFlag.Flags.experimental
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.2.41"
}

version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8", "1.2.41"))
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.22.5")
    compile("com.github.kittinunf.fuel:fuel:1.13.0")
    compile("com.github.kittinunf.fuel:fuel-coroutines:1.13.0")
    compile("com.github.kittinunf.fuel:fuel-moshi:1.13.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

// Configure `jar` task to set the main class name and to include all
// project dependencies
val jar: Jar by tasks
jar.apply {
    manifest.attributes.apply {
        put("Main-Class", "AppKt")
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    from(configurations.runtime.map({ file ->
        if (file.isDirectory) file else zipTree(file)
    }))
}
