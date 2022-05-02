plugins {
    id("java")
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
    compileOnly("eu.okaeri:okaeri-configs-yaml-bukkit:3.4.2")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
}