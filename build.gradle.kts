plugins {
	java
	id("org.springframework.boot") version "4.0.2"
	id("io.spring.dependency-management") version "1.1.7"
}

// Frontend build configuration
val frontendSourceDir = file("src/main/vue")
val frontendOutputDir = file("build/resources/main/public")

// NPM Install task
tasks.register<Exec>("npmInstall") {
	group = "frontend"
	description = "Install npm dependencies"
	workingDir(frontendSourceDir)
	commandLine("cmd", "/c", "npm install")
}

// NPM Build task
tasks.register<Exec>("npmBuild") {
	group = "frontend"
	description = "Build Vue.js frontend"
	workingDir(frontendSourceDir)
	commandLine("cmd", "/c", "npm run build")
	dependsOn("npmInstall")
}

// NPM Test task
tasks.register<Exec>("npmTest") {
	group = "frontend"
	description = "Run Vue.js tests (Vitest)"
	workingDir(frontendSourceDir)
	commandLine("cmd", "/c", "set CI=true && npm run test")
}

// Vue Build aggregate task (install + build)
tasks.register("vueBuild") {
	group = "frontend"
	description = "Full Vue.js build (install dependencies + build)"
	dependsOn("npmInstall")
	dependsOn("npmBuild")
}

// Vue Clean task
tasks.register<Delete>("vueClean") {
	group = "frontend"
	description = "Clean Vue.js build output"
	delete(frontendOutputDir)
}

// Process resources depends on vueBuild
tasks.processResources {
	dependsOn("vueBuild")
}

// Build task depends on vueBuild
tasks.named("build") {
	dependsOn("vueBuild")
}

// Test task depends on npmTest
tasks.named("test") {
	dependsOn("npmTest")
}

// Clean task depends on vueClean
tasks.named("clean") {
	dependsOn("vueClean")
}

group = "twin"
version = "0.0.1-SNAPSHOT"
description = "SpringTwin MCP agent"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.modulith:spring-modulith-starter-core:2.0.2")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	testCompileOnly("org.projectlombok:lombok")
	testAnnotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testImplementation("org.springframework.boot:spring-boot-starter-data-neo4j-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
	testImplementation("org.springframework.modulith:spring-modulith-starter-test:2.0.2")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
