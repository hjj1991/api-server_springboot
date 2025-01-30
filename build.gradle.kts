val restdocsApiSpecVersion = "0.18.2"

plugins {
    val kotlinPluginVersion = "2.0.0"
    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.spring") version kotlinPluginVersion apply false
    kotlin("plugin.jpa") version kotlinPluginVersion apply false
    kotlin("kapt") version kotlinPluginVersion
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
    // gRPC 플러그인 추가
    id("com.google.protobuf") version "0.9.4"
}

group = "com.hjj.apiserver"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

ktlint {
    debug.set(false)
    verbose.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.kapt") // 하위 모듈에 kapt 플러그인 적용
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring") //all-open
    apply(plugin = "kotlin-jpa")

    dependencies {
        implementation("org.springframework.boot:spring-boot-starter-webflux")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        // gRPC & Protobuf
        implementation("io.grpc:grpc-netty-shaded:1.70.0")
        implementation("io.grpc:grpc-protobuf:1.70.0")
        implementation("io.grpc:grpc-stub:1.70.0")
        implementation("com.google.protobuf:protobuf-java:4.29.3")
        implementation("com.google.protobuf:protobuf-kotlin:4.29.3")

        // gRPC Kotlin
        implementation("io.grpc:grpc-kotlin-stub:1.4.1")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

        testImplementation("org.springframework.boot:spring-boot-starter-test")


    }

    repositories {
        mavenCentral()
    }



    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

tasks.withType<JavaCompile> {
    options.annotationProcessorPath = configurations.kapt.get()
}

tasks {
    bootJar {
        enabled = false
    }

    jar {
        enabled = false
    }
}
