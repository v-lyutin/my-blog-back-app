plugins {
    id("java")
    id("war")
}

group = "com.amit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val junitVersion: String by project

dependencies {
    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.war {
    archiveBaseName.set("my-blog-backend-app")
}

tasks.test {
    useJUnitPlatform()
}