plugins {
    val kotlinVersion = "1.6.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion
    id("net.mamoe.mirai-console") version "2.10.1"
}

group = "org.seiki"
version = "1.0-SNAPSHOT"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    maven("https://maven.aliyun.com/repository/central")
    maven("https://maven.aliyun.com/repository/google/")
    maven("https://maven.aliyun.com/repository/jcenter/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://jitpack.io")
    mavenCentral()
}

val legacy: Boolean = false

fun skikoAwt(ver: String) = "org.jetbrains.skiko:skiko-awt-runtime-$ver"

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")

    if (legacy) {
        val skikoVer = "0.7.12"
        implementation(skikoAwt("windows-x64:$skikoVer"))
        implementation(skikoAwt("linux-x64:$skikoVer"))
        implementation(skikoAwt("linux-arm64:$skikoVer"))
    } else {
        val smVer = "1.0.6"
        compileOnly("com.github.LaoLittle:SkikoMirai:$smVer")
        testImplementation("com.github.LaoLittle:SkikoMirai:$smVer")
        testImplementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.18")
    }

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}