plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "vuce.cp"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

extra["springCloudVersion"] = "2025.0.0"
val fwkSecurity = "2.0.0"
val swaggerParserVersion = "2.1.34"
val swaggerModelsVersion = "2.2.34"
val swaggerAnnotationsVersion = "2.2.34"
val swaggerIntegrationVersion = "2.2.34"

dependencies {


    implementation("org.springframework.boot:spring-boot-starter-webflux"){
    }

    implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux"){
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-core")
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-http")
    }
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.springframework.security:spring-security-config"){
        exclude(group = "org.springframework.security", module = "spring-security-core")
    }
    implementation("pe.gob.vuce.cp.framework:vuce-cp-fwk-security:${fwkSecurity}")

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")

    // OpenAPI/Swagger dependencies
    implementation("io.swagger.parser.v3:swagger-parser:${swaggerParserVersion}")
    implementation("io.swagger.core.v3:swagger-models:${swaggerModelsVersion}")
    implementation("io.swagger.core.v3:swagger-annotations:${swaggerAnnotationsVersion}")
    implementation("io.swagger.core.v3:swagger-integration:${swaggerIntegrationVersion}")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    runtimeOnly("org.springframework.cloud:spring-cloud-starter-loadbalancer"){
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-core")
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-http")
        exclude(group = "io.netty")

        exclude(group = "org.springframework.security", module = "spring-security-crypto")
    }

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}