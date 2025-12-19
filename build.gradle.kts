plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.3.0"
}

group = "pe.gob.vuce.cp.bs"
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
val sprintBootParent = "3.3.0"
val mockitoCore = "3.12.4"
val mockitoInline = "5.0.0"
val mockitoJunit = "3.12.4"
val openApiWebMvcUiVersion = "2.5.0"
val logstashLogbackEncoderVersion = "7.0.1"
val poiVersion = "5.2.3"
val jasperReportsVersion = "6.21.3"
val bcprovVersion = "1.78"
val swaggerUiVersion = "5.18.0"
val jfreeChartVersion = "1.5.5"
val nettyHandlerVersion = "4.1.127.Final"
val global = "1.0.0"
val tomcatVersion = "10.1.44"
val fileuploadVersion = "1.6.0"
val kafkaClientVersion = "3.9.1"
val kafkaSpringVersion = "3.3.10"
val okhttp3Version = "4.12.0"
val okioVersion = "3.5.0"
val beanutilsVersion = "1.11.0"

dependencies {

    // Spring Boot starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring cloud
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    // Kafka dependencies (managed by Spring Boot BOM)
    implementation("org.springframework.kafka:spring-kafka")

    // OpenAPI / Swagger
    implementation("com.google.code.gson:gson:$gsonVersion")
    implementation("io.swagger.core.v3:swagger-annotations:$swaggerVersion")
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
//    implementation("org.springframework.boot:spring-boot-starter-parent:$sprintBootParent")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$openApiWebMvcUiVersion")
//    implementation("net.logstash.logback:logstash-logback-encoder:$logstashLogbackEncoderVersion")
    implementation("org.webjars:swagger-ui:$swaggerUiVersion")
    implementation("org.jfree:jfreechart:$jfreeChartVersion")
//    implementation("pe.gob.vuce.cp.framework:vuce-cp-fwk-globallogger:$global")

    implementation ("org.apache.poi:poi:$poiVersion")
    implementation ("org.apache.poi:poi-ooxml:$poiVersion")
    implementation("net.sf.jasperreports:jasperreports:$jasperReportsVersion")
    implementation ("net.sf.jasperreports:jasperreports-fonts:$jasperReportsVersion")
//    implementation("org.bouncycastle:bcprov-jdk18on:$bcprovVersion")

    // Monitoring dependencies
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

//    implementation("org.springframework.data:spring-data-redis")
//    implementation("org.springframework.data:spring-data-commons")

    implementation("commons-fileupload:commons-fileupload:${fileuploadVersion}")


    runtimeOnly("org.postgresql:postgresql")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor:$mapstructProcessVersion")

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
    outputDir.set("${layout.buildDirectory.get().asFile}/generated/openapi")
    apiPackage.set("pe.gob.vuce.cp.bs.consultaficha.query.api")
    modelPackage.set("pe.gob.vuce.cp.bs.consultaficha.query.model")
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
    srcDir("${layout.buildDirectory.get().asFile}/generated/openapi/src/main/java")
}

jacoco {
    toolVersion = "0.8.8"
}

val jacocoExclude = listOf("pe/gob/vuce/cp/bs/consultaficha/query/api/**", "pe/gob/vuce/cp/bs/consultaficha/query/model/**")
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