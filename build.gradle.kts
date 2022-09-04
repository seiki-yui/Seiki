plugins {
    val kotlinVersion = "1.6.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.12.2"
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

fun skikoAwt(ver: String) = "org.jetbrains.skiko:skiko-awt-runtime-$ver"

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")
    implementation("cn.hutool:hutool-cron:5.8.5")
    val smVer = "1.0.8"
    compileOnly("com.github.LaoLittle:SkikoMirai:$smVer")
    testImplementation("com.github.LaoLittle:SkikoMirai:$smVer")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}