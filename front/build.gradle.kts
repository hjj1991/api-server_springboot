import com.google.protobuf.gradle.id

plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
    id("com.google.protobuf")
}

group = "ninja.sundry"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
    implementation(project(":core")) // core 모듈 의존성 추가

	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// gRPC 자동 코드 생성 설정
protobuf {
    // Protobuf 컴파일러를 지정하여 .proto 파일을 컴파일합니다.
    protoc {
        artifact = "com.google.protobuf:protoc:4.29.3"
    }

    // gRPC 플러그인을 설정하여 Protobuf 파일로부터 gRPC 관련 코드를 생성합니다.
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.70.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.70.0:jdk8@jar"
        }
    }
    // 모든 프로토콜 버퍼 작업에 대해 gRPC 플러그인을 적용합니다.
    generateProtoTasks {
        all().forEach { generateProtoTask ->
            generateProtoTask.plugins {
                id("grpc")
                id("grpckt")
            }
            generateProtoTask.builtins {
                id("kotlin")
            }
        }
    }
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("prepareKotlinBuildScriptModel"){}
