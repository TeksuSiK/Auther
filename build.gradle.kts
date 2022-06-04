import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "pl.teksusik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.13.2-R0.1-SNAPSHOT")

    implementation("com.zaxxer:HikariCP:5.0.1")

    implementation("eu.okaeri:okaeri-configs-yaml-bukkit:3.4.2")

    implementation("net.kyori:adventure-text-minimessage:4.10.1")
    implementation("net.kyori:adventure-platform-bukkit:4.1.0")

    implementation("at.favre.lib:bcrypt:0.9.0")
    implementation("com.warrenstrange:googleauth:1.5.0")
}

tasks.named<ShadowJar>("shadowJar") {
    relocate("com.zaxxer", "pl.teksusik.auther.libs.com.zaxxer")

    relocate("eu.okaeri", "pl.teksusik.auther.libs.eu.okaeri")

    relocate("net.kyori", "pl.teksusik.auther.libs.net.kyori")

    relocate("at.favre", "pl.teksusik.auther.libs.at.favre")
    relocate("com.warrenstrange", "pl.teksusik.auther.libs.com.warrenstrange")
}