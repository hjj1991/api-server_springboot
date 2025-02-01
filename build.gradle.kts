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
    apply(plugin = "com.google.protobuf")

    extra["springGrpcVersion"] = "0.3.0"

    dependencyManagement {
        imports {
            mavenBom("org.springframework.grpc:spring-grpc-dependencies:${property("springGrpcVersion")}")
        }
    }

    dependencies {
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.jetbrains.kotlin:kotlin-reflect")

        implementation("io.grpc:grpc-services")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
        testImplementation("org.springframework.grpc:spring-grpc-test")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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
