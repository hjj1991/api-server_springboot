import com.google.protobuf.gradle.id

plugins {
    id("com.ewerk.gradle.plugins.querydsl") version "1.0.10"
    kotlin("plugin.jpa")
    kotlin("plugin.spring") // 루트에서 정의한 버전 사용
    kotlin("kapt") // 루트에서 정의한 버전 사용
    id("com.google.protobuf")
}


allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

group = "ninja.sundry"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}


dependencies {
    implementation(project(":core")) // core 모듈 의존성 추가

    implementation("org.springframework.grpc:spring-grpc-spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.9.0")
    implementation("com.google.firebase:firebase-admin:9.1.1")
    implementation("commons-io:commons-io:2.11.0")
    implementation("com.cwbase:logback-redis-appender:1.1.6")
    implementation("com.mysql:mysql-connector-j:8.4.0")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.4.0")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    implementation("com.querydsl:querydsl-core:5.0.0")
    implementation("io.github.microutils:kotlin-logging:4.0.0-beta-2")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")

    kapt("org.springframework.boot:spring-boot-configuration-processor")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-kotlin-codegen:5.0.0")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:mariadb:1.18.0")
    testImplementation("org.testcontainers:mysql:1.20.1")
    testImplementation("com.redis:testcontainers-redis:2.2.2")
    testImplementation("org.testcontainers:junit-jupiter:1.18.0")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}

tasks.register("prepareKotlinBuildScriptModel"){}
