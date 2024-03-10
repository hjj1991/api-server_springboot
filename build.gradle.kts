import java.util.Locale

val restdocsApiSpecVersion = "0.18.2"

plugins {
    val restdocsApiSpecVersion = "0.18.2"
    val kotlinPluginVersion = "1.9.22"
    kotlin("jvm") version kotlinPluginVersion
    kotlin("plugin.spring") version kotlinPluginVersion
    kotlin("plugin.jpa") version kotlinPluginVersion
    kotlin("plugin.allopen") version kotlinPluginVersion
    kotlin("kapt") version kotlinPluginVersion
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
    id("com.epages.restdocs-api-spec") version restdocsApiSpecVersion
    id("com.palantir.docker") version "0.35.0"
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

ktlint {
    debug.set(false)
    verbose.set(false)
    outputToConsole.set(true)
    ignoreFailures.set(false)
    enableExperimentalRules.set(false)
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web-services")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
    implementation("org.modelmapper:modelmapper:3.1.1")
    implementation("io.jsonwebtoken:jjwt-api:0.12.5")
    implementation("io.springfox:springfox-boot-starter:3.0.0")
    implementation("org.imgscalr:imgscalr-lib:4.2")
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.seleniumhq.selenium:selenium-java:4.3.0")
    implementation("com.cwbase:logback-redis-appender:1.1.6")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")

    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-kotlin-codegen:5.0.0")
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    asciidoctorExt("org.springframework.restdocs:spring-restdocs-asciidoctor:2.0.5.RELEASE")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("com.epages:restdocs-api-spec-mockmvc:$restdocsApiSpecVersion")

    testImplementation("org.testcontainers:mariadb:1.18.0")
    testImplementation("org.testcontainers:junit-jupiter:1.18.0")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}
//  spring rest docs를 swagger와 함께 쓰기 위해 주석처리
val snippetsDir by extra { file("build/generated-snippets") }

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

    docker {
        println(bootJar.get().outputs.files)

        // 이미지 이름
        name = rootProject.name.lowercase(Locale.getDefault()) + ':' + version

        // 어떤 Dockerfile
        setDockerfile(file("Dockerfile"))

        // 어떤 파일들을 Dockerfile 에 복사할 것인가
        files(bootJar.get().outputs.files)

        // Dockerfile 에 전달할 인자
        buildArgs(mapOf("JAR_FILE" to bootJar.get().outputs.files.singleFile.name))
    }
}
