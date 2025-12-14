plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.owasp.dependencycheck") version "12.1.6"
}

allprojects {
    apply(plugin = "org.owasp.dependencycheck")
}

group = "pe.gob.vuce.cp.sp.sample.experience"
version = "1.0.0"

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

repositories {
    mavenCentral()

}

extra["springCloudVersion"] = "2025.0.0"
extra["netty.version"] = "4.1.128.Final"
val projectreactorNettyVersion = "1.2.11"

dependencies {


    implementation("org.springframework.boot:spring-boot-starter-webflux"){
//        exclude(group = "io.netty")
        /*        exclude(group = "io.projectreactor.netty", module = "reactor-netty-http")
                exclude(group = "io.projectreactor.netty", module = "reactor-netty-core")*/
    }

    implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux"){
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-core")
        exclude(group = "io.projectreactor.netty", module = "reactor-netty-http")
    }

    implementation("org.springframework.security:spring-security-config"){
        exclude(group = "org.springframework.security", module = "spring-security-core")
    }
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
        mavenBom("io.netty:netty-bom:${property("netty.version")}")
    }
    /*    dependencies {
            dependency("io.projectreactor.netty:reactor-netty-http:${projectreactorNettyVersion}")
            dependency("io.projectreactor.netty:reactor-netty-core:${projectreactorNettyVersion}")
        }*/
}


tasks.getByName<Jar>("jar") {
    enabled = false
}

/*configurations.all {
    resolutionStrategy {
        force(
            "io.netty:netty-transport:${property("netty.version")}",
            "io.netty:netty-codec-http:${property("netty.version")}",
            "io.netty:netty-handler:${property("netty.version")}",
            "io.netty:netty-resolver-dns:${property("netty.version")}"
        )
    }
}*/
dependencyCheck {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
    nvd {
        apiKey = System.getenv("NVD_API_KEY") ?: project.findProperty("nvd.api.key") as String? ?: ""
    }
}
