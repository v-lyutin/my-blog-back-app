plugins {
    id("java")
    id("war")
}

group = "com.amit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val springVersion: String by project
val jakartaServletApiVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("org.springframework:spring-context:$springVersion")
    implementation("org.springframework:spring-webmvc:$springVersion")

    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaServletApiVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.war {
    archiveFileName.set("my-blog-back-app.war")
}

tasks.test {
    useJUnitPlatform()
}