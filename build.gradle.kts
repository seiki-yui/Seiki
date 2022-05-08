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
// 使用SkikoMirai 1.0.2 jar解包的dll,和SkikoLibs的icudtl.dat文件 即可兼容windows7
// 这一切一定都是LaoLittle的错 哼

fun skikoAwt(ver: String) = "org.jetbrains.skiko:skiko-awt-runtime-$ver"

dependencies {
    implementation("com.google.code.gson:gson:2.9.0")

    if (legacy) {
        val skikoVer = "0.7.16"
        implementation(skikoAwt("windows-x64:$skikoVer"))
        implementation(skikoAwt("linux-x64:$skikoVer"))
        implementation(skikoAwt("linux-arm64:$skikoVer"))
    } else {
        val smVer = "1.0.7"
        compileOnly("com.github.LaoLittle:SkikoMirai:$smVer")
        testImplementation("com.github.LaoLittle:SkikoMirai:$smVer")
    }

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
}

tasks {
    compileKotlin {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}