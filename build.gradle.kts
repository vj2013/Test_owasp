plugins {
    java
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "vuce.cp"
version = "0.0.1-SNAPSHOT"


tasks.getByName<Jar>("jar") {
    enabled = false
}