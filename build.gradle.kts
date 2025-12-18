plugins {
    java
    id("jacoco")
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.3.0"
}

group = "pe.gob.vuce.cp.bs.arribozarpe.query"
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
val openfeignVersion = "4.2.3"
val openapiVersion = "2.8.14"
val gsonVersion = "2.9.0"
val itextpdfVersion = "5.5.12"
val poiVersion = "5.2.3"
val fileuploadVersion = "1.6.0"
val junitVersion = "4.13.1"

dependencies {
    //	Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${openapiVersion}")

    // Monitoring dependencies
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Comunicación externa
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign:$openfeignVersion")
    implementation("org.springframework.kafka:spring-kafka")

    // Mapeo y utilitarios
    implementation("org.mapstruct:mapstruct:$mapstructVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")

    // Reportes / Archivos
    implementation("com.itextpdf:itextpdf:$itextpdfVersion");
    implementation("org.apache.poi:poi-ooxml:$poiVersion");
    implementation("commons-fileupload:commons-fileupload:${fileuploadVersion}")

    // Base de datos
    runtimeOnly("org.postgresql:postgresql")

    //	Lombok & MapStruct (compile-time)
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor ("org.mapstruct:mapstruct-processor:$mapstructVersion")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("junit:junit:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}
openApiGenerate {
    generatorName.set("spring")
    inputSpec.set("$projectDir/src/main/resources/openapi.yml")
    outputDir.set("$buildDir/generated/openapi")
    apiPackage.set("pe.gob.vuce.cp.bs.arribozarpe.query.contract.api")
    modelPackage.set("pe.gob.vuce.cp.bs.arribozarpe.query.contract.model")
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
        "oas3" to "true"
    ))
}
tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.named("processResources"){
    dependsOn(tasks.openApiGenerate)
}
tasks.named("compileJava"){
    dependsOn(tasks.openApiGenerate)
}
tasks.getByName<Jar>("jar") { enabled = false }

java.sourceSets["main"].java {
    srcDir("$buildDir/generated/openapi/src/main/java")
}

jacoco {
    toolVersion = "0.8.7"
}

val jacocoExclude = listOf("**/build/**", "**/generated/**")
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
