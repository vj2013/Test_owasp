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

dependencyManagement {
    imports {
        mavenBom("io.netty:netty-bom:${property("netty.version")}")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
}

configurations.all {
    resolutionStrategy {
        // **IMPORTANTE:** Eliminar cualquier 'force' anterior para reactor-netty-http
        // y asegurar que los componentes base de Netty se fuerzan a la versión del BOM.

        val nettyVersion = property("netty.version") as String

        // 3. Estrategia de Forzado: Forzar explícitamente los componentes de Netty
        // a la versión definida, lo que anulará cualquier versión antigua.
        force("io.netty:netty-codec-http:$nettyVersion") // Corrige CVE-2019-20444 y otros.
        force("io.netty:netty-handler:$nettyVersion")
        force("io.netty:netty-buffer:$nettyVersion")
        force("io.netty:netty-common:$nettyVersion")
        force("io.netty:netty-transport:$nettyVersion")
        // Agregar cualquier otro artefacto 'io.netty' que el escaneo siga reportando.
    }
}

tasks.getByName<Jar>("jar") {
    enabled = false
}


