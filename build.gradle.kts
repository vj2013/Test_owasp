plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
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
extra["netty.version"] = "4.1.128.Final"

configurations.all {
    resolutionStrategy {
        force("io.projectreactor.netty:reactor-netty-http:1.2.12")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:${property("netty.version")}")
    }
}

configurations.all {
    resolutionStrategy {
        // Asegúrate de usar la versión correcta de Netty que contiene los arreglos
        val nettyVersion = property("netty.version") as String

        // Forzar las dependencias base de Netty a la versión del BOM
        // Estos son los componentes base que tienen las vulnerabilidades reportadas
        force("io.netty:netty-codec-http:$nettyVersion")
        force("io.netty:netty-handler:$nettyVersion")
        force("io.netty:netty-buffer:$nettyVersion")
        // ... otras librerías de Netty si el escaneo las sigue reportando
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}


