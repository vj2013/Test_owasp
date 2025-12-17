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

val mapstructVersion = "1.5.5.Final"
val mapstructProcessVersion = "1.5.5.Final"
val swaggerVersion = "2.2.22"
val gsonVersion = "2.8.9"
val mockitoCore = "3.12.4"
val mockitoInline = "5.0.0"
val mockitoJunit = "3.12.4"
val logstashLogbackEncoderVersion = "7.0.1"
val poiVersion = "5.4.0"
val jasperReportsVersion = "7.0.3"
val bcprovVersion = "1.78"
val swaggerUiVersion = "5.18.0"
val jfreeChartVersion = "1.5.5"
val globalLoggerVersion = "1.0.0"
val kafkaClientVersion = "3.9.1"

val fileuploadVersion = "1.6.0"
val beanutilsVersion = "1.11.0"

dependencies {
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructProcessVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("io.swagger.core.v3:swagger-annotations:$swaggerVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("org.apache.poi:poi:$poiVersion")
    implementation("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("net.sf.jasperreports:jasperreports:$jasperReportsVersion")
    implementation("net.sf.jasperreports:jasperreports-fonts:$jasperReportsVersion")
    implementation("org.bouncycastle:bcprov-jdk18on:$bcprovVersion")
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")
    implementation("org.jfree:jfreechart:$jfreeChartVersion")
//    implementation("pe.gob.vuce.cp.framework:vuce-cp-fwk-globallogger:$globalLoggerVersion")
    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    //CB
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    // Monitoring dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    implementation("commons-beanutils:commons-beanutils:${beanutilsVersion}")
    implementation("commons-fileupload:commons-fileupload:${fileuploadVersion}")

    implementation("org.apache.kafka:kafka-clients:${kafkaClientVersion}")

    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.awaitility:awaitility")
    testImplementation("org.mockito:mockito-core:$mockitoCore")
    testImplementation("org.mockito:mockito-inline:$mockitoInline")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoJunit")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
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

openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yml")
    outputDir.set("$buildDir/generated/openapi")
    apiPackage.set("pe.gob.vuce.cp.bs.fichatecnica.query.api")
    modelPackage.set("pe.gob.vuce.cp.bs.fichatecnica.query.model")
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
        "reactivex" to "true",
        "suppressAllWarnings" to "false"
    ))
}

tasks.named("processResources") {
    dependsOn(tasks.openApiGenerate)
}
tasks.named("compileJava") {
    dependsOn(tasks.openApiGenerate)
}
tasks.named<Jar>("jar") {
    enabled = false
}

java.sourceSets["main"].java {
    srcDir("$buildDir/generated/openapi/src/main/java")
}

jacoco {
    // Upgrade to support running tests on newer JDKs (e.g., Java 21 -> class file version 65)
    toolVersion = "0.8.12"
}

val jacocoExclude = listOf("pe/gob/vuce/cp/bs/fichatecnica/query/api/**", "pe/gob/vuce/cp/bs/fichatecnica/query/model/**")
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