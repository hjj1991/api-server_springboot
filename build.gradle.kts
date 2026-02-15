
val restdocsApiSpecVersion = "0.19.4"

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.palantir.gradle.docker:gradle-docker:0.37.0")
    }
}

plugins {
    val restdocsApiSpecVersion = "0.19.4"
    val kotlinPluginVersion = "2.2.21"
    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.spring") version kotlinPluginVersion
    kotlin("plugin.jpa") version kotlinPluginVersion
    kotlin("plugin.allopen") version kotlinPluginVersion
    kotlin("kapt") version kotlinPluginVersion
//    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "4.0.5"
    id("com.epages.restdocs-api-spec") version restdocsApiSpecVersion
}

apply(plugin = "com.palantir.docker")

kotlin {
    jvmToolchain(25)
}

val asciidoctorExt: Configuration by configurations.creating

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
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:2.0.0")
    implementation("org.modelmapper:modelmapper:3.2.6")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.1")
    implementation("commons-io:commons-io:2.21.0")
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.0")
    implementation("com.querydsl:querydsl-jpa:5.1.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.1.0")
    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.2.3")
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("com.querydsl:querydsl-apt:5.1.0:jakarta")
    kapt("com.querydsl:querydsl-kotlin-codegen:5.1.0")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor:4.0.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:$restdocsApiSpecVersion")

    testImplementation("org.testcontainers:postgresql:1.21.4")
    testImplementation("com.redis:testcontainers-redis:2.2.4")
    testImplementation("org.testcontainers:junit-jupiter:1.21.4")

}
//  spring rest docs를 swagger와 함께 쓰기 위해 주석처리
val snippetsDir by extra { file("build/generated-snippets") }

tasks.withType<JavaCompile> {
    options.release.set(24)
    options.annotationProcessorPath = configurations.kapt.get()
}

tasks {
    test {
        outputs.dir(snippetsDir)
        useJUnitPlatform()
    }
    asciidoctor {
        dependsOn(test)
        setSourceDir(snippetsDir)
        configurations("asciidoctorExt")
    }

    val copyHTML =
        register("copyHTML") {
            dependsOn("asciidoctor")
            delete(file("src/main/resources/static/docs"))
            copy {
                from(file("${layout.buildDirectory.get()}/docs/asciidoc"))
                into(file("src/main/resources/static/docs"))
            }
        }

    val registerOpenapi3 =
        register("registerOpenapi3") {
            delete(file("src/main/resources/static/swagger-ui/openapi3.yaml")) // 기존 OAS 파일 삭제
            copy {
                from(file("${layout.buildDirectory.get()}/api-spec/openapi3.yaml")) // 복제할 OAS 파일 지정
                into(file("src/main/resources/static/swagger-ui/")) // 타겟 디렉터리로 파일 복제
            }
            dependsOn("openapi3")
        }

    build {
        dependsOn(copyHTML, registerOpenapi3)
        version = ""
    }

    bootJar {
        dependsOn(asciidoctor, copyHTML, registerOpenapi3)
        from(asciidoctor.get().outputDir) {
            into(file("src/main/resources/static/docs"))
        }
    }

    openapi3 {
        setServer("http://localhost:8080")
        title = "restdocs-swagger API Documentation"
        description = "Spring REST Docs with SwaggerUI."
        version = "0.0.1"
        format = "yaml"
    }

    jar {
        enabled = false
    }
}
