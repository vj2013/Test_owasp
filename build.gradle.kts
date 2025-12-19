plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.3.0"
}

group = "vuce.cp"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"
var webmvcVersion = "2.5.0"
var logbackVersion = "7.0.1"
var mapstructVersion = "1.5.5.Final"
var bcprovVersion = "1.79"
var swaggerUiVersion = "5.18.0"
var globalLoggerVersion="1.0.0"
var commonsLangVersion="3.18.0"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$webmvcVersion") {
        exclude(group = "org.apache.commons", module = "commons-lang3")
    }
    implementation("org.apache.commons:commons-lang3:${commonsLangVersion}") // Force secure version (CVE-2025-48924)
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("net.logstash.logback:logstash-logback-encoder:$logbackVersion")
    implementation("org.bouncycastle:bcprov-jdk18on:$bcprovVersion") // Updated to 1.79 (CVE-2025-8916)
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")
//    implementation("pe.gob.vuce.cp.framework:vuce-cp-fwk-globallogger:${globalLoggerVersion}")

    // Monitoring dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Kafka dependencies (managed by Spring Boot BOM)
    implementation("org.springframework.kafka:spring-kafka")

    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructVersion")
    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.awaitility:awaitility")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.getByName<Jar>("jar") { enabled = false }

jacoco {
    toolVersion = "0.8.12"
}

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yml")
    outputDir.set("${layout.buildDirectory.get().asFile}/generated/openapi")
    apiPackage.set("pe.gob.vuce.cp.bs.fichasanitaria.query.api")
    modelPackage.set("pe.gob.vuce.cp.bs.fichasanitaria.query.model")
    configOptions.set(mapOf(
        "addRequestHeadersToAPI" to "true",
        "useSpringBoot3" to "true",
        "dateLibrary" to "java17",
        "generateApis" to "true",
        "generateModels" to "true",
        "interfaceOnly" to "true",
        "serializableModel" to "true",
        "useBeanValidation" to "true",
        "useTags" to "true",
        "implicitHeaders" to "true",
        "openApiNullable" to "false",
        "oas3" to "true",
        "reactivex" to "true"
    ))
}

tasks.named("processResources") {
    dependsOn(tasks.openApiGenerate)
}
tasks.named("compileJava") {
    dependsOn(tasks.openApiGenerate)
}

java.sourceSets["main"].java {
    srcDir("${layout.buildDirectory.get().asFile}/generated/openapi/src/main/java")
}

val jacocoExclude = listOf(
    "pe/gob/vuce/cp/bs/fichasanitaria/query/api/**",     // OpenAPI generated
    "pe/gob/vuce/cp/bs/fichasanitaria/query/model/**"    // Models generated
)

tasks.withType<JacocoReport> {
    dependsOn(tasks.test)

    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.required.set(true)
    }

    afterEvaluate {
        classDirectories.setFrom(files(classDirectories.files.map {
            fileTree(it).exclude(jacocoExclude)
        }))
    }
}