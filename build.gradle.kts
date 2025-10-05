plugins {
    id("java")
    id("war")
}

group = "com.amit"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val springCoreVersion: String by project
val springDataJdbcVersion: String by project
val jakartaServletApiVersion: String by project
val junitVersion: String by project

dependencies {
    implementation("org.springframework:spring-context:$springCoreVersion")
    implementation("org.springframework:spring-webmvc:$springCoreVersion")
    implementation("org.springframework:spring-jdbc:$springCoreVersion")
    implementation("org.springframework.data:spring-data-jdbc:$springDataJdbcVersion")

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