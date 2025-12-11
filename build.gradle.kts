plugins {
    java
    id("org.springframework.boot") version "3.5.7"
//    id("org.springframework.boot") version "4.0.0" --gradle 8.14 o superior
    id("io.spring.dependency-management") version "1.1.7"
//    id("org.owasp.dependencycheck") version "12.1.6"
}

/*allprojects {
    apply(plugin = "org.owasp.dependencycheck")
}*/

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

//extra["springCloudVersion"] = "2025.0.0"

//extra["netty.version"] = "4.1.128.Final"

configurations.all {
    resolutionStrategy {
        force("io.projectreactor.netty:reactor-netty-http:1.2.12")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    // Forzar versión específica de reactor-netty
    implementation("io.projectreactor.netty:reactor-netty-http:1.2.12")
//    implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux")
//    implementation("org.springframework.boot:spring-boot-starter-web")
//    implementation("org.springframework.boot:spring-boot-starter-data-redis")
//    implementation("org.springframework.boot:spring-boot-starter-security")


    /*    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
        implementation("org.springframework.cloud:spring-cloud-gateway-server-webflux")*/
}

/*dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:${property("netty.version")}")
    }
}*/


tasks.getByName<Jar>("jar") {
    enabled = false
}

/*
dependencyCheck {
//    suppressionFile = "${project.rootDir}/dependency-check-suppression.xml"
//    suppressionFile = "dependency-check-suppression.xml"
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
    nvd {
        apiKey = System.getenv("NVD_API_KEY") ?: project.findProperty("nvd.api.key") as String? ?: ""

//        outputDirectory = "${project.buildDir}/reports/dependency-check"
    }
}
*/


