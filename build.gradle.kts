// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    val agpVersion = "8.0.2"
    id("com.android.application") version "8.0.2" apply false
    id("com.android.library") version agpVersion apply false

    val kotlinVersion = "1.8.20"
    kotlin("android") version kotlinVersion apply false
    kotlin("jvm") version kotlinVersion apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
