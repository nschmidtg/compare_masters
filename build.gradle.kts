plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  id("org.springframework.boot") version "3.5.3"
  id("io.spring.dependency-management") version "1.1.7"
  kotlin("plugin.jpa") version "1.9.25"
  id("org.openapi.generator") version "7.8.0"
  id("com.diffplug.spotless") version "6.8.0"
}

group = "com.nschmidtg"
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
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  developmentOnly("org.springframework.boot:spring-boot-devtools")
  runtimeOnly("com.mysql:mysql-connector-j")
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.mockito.kotlin:mockito-kotlin:5.2.1")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("com.h2database:h2")
  implementation("org.openapitools:jackson-databind-nullable:0.2.6")
  implementation("jakarta.annotation:jakarta.annotation-api")
  compileOnly("jakarta.servlet:jakarta.servlet-api")
  implementation("io.jsonwebtoken:jjwt-api:0.12.5")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.5")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

openApiGenerate {
  generatorName.set("spring")
  inputSpec.set("$rootDir/specs/spec.yaml")
  packageName.set("com.nschmidtg")
  apiPackage.set("com.nschmidtg.comparemasters.infrastructure.web.api")
    modelPackage.set("com.nschmidtg.comparemasters.infrastructure.web.model")
  configOptions.putAll(
    mapOf(
      "delegatePattern" to "false",
      "interfaceOnly" to "true",
      "useResponseEntity" to "true",
      "useJakartaEe" to "true"
    )
  )
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
  kotlin {
    target("src/**/*.kt")
    ktfmt().googleStyle().configure {
      it.setMaxWidth(80)
      it.setBlockIndent(4)
      it.setContinuationIndent(4)
      it.setRemoveUnusedImport(true)
    }
  }
}


tasks.compileKotlin.get().dependsOn(tasks.openApiGenerate)
tasks.compileKotlin.get().dependsOn(tasks.spotlessApply)
sourceSets {
  main {
    java {
      srcDir("${layout.buildDirectory.get().asFile}/generate-resources/main/src/main/java")
    }
  }
  val intTest by creating {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
    compileClasspath += sourceSets.test.get().compileClasspath
    runtimeClasspath += sourceSets.test.get().runtimeClasspath
  }
}

val intTest = task<Test>("intTest") {
    description = "Runs integration tests."
    group = "verification"

    testClassesDirs = sourceSets["intTest"].output.classesDirs
    classpath = sourceSets["intTest"].runtimeClasspath
    useJUnitPlatform()
}

configurations {
    val intTestImplementation by getting {
        extendsFrom(configurations.testImplementation.get())
    }
    val intTestRuntimeOnly by getting {
        extendsFrom(configurations.testRuntimeOnly.get())
    }
}
