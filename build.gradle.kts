
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.palantir.gradle.docker:gradle-docker:0.37.0")
    }
}

plugins {
    val kotlinPluginVersion = "2.2.21"
    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.spring") version kotlinPluginVersion
    kotlin("plugin.jpa") version kotlinPluginVersion
    kotlin("plugin.allopen") version kotlinPluginVersion
    kotlin("kapt") version kotlinPluginVersion
//    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
}

apply(plugin = "com.palantir.docker")

kotlin {
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_24)
    }
}

// plugins, dependencies와 같은 Level (즉 build.gradle 최상단)
allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "com.hjj.apiserver"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

//ktlint {
//    debug.set(false)
//    verbose.set(false)
//    outputToConsole.set(true)
//    ignoreFailures.set(false)
//    enableExperimentalRules.set(false)
//}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("commons-io:commons-io:2.21.0")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.0")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-kotlin-codegen:5.1.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

}
tasks.withType<JavaCompile> {
    options.release.set(24)
    options.annotationProcessorPath = configurations.kapt.get()
}

tasks {
    test {
        useJUnitPlatform()
    }

    jar {
        enabled = false
    }
}
