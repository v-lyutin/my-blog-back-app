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
val jakartaServletApiVersion: String by project
val junitVersion: String by project
val postgresqlVersion: String by project
val jacksonVersion: String by project
val mockitoVersion: String by project
val testContainersVersion: String by project

dependencies {
    implementation("org.springframework:spring-context:$springCoreVersion")
    implementation("org.springframework:spring-webmvc:$springCoreVersion")
    implementation("org.springframework:spring-jdbc:$springCoreVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")

    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")

    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaServletApiVersion")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.springframework:spring-test:$springCoreVersion")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-junit-jupiter:$mockitoVersion")

    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")

    testImplementation("org.postgresql:postgresql:$postgresqlVersion")
}

tasks.war {
    archiveFileName.set("my-blog-back-app.war")
}

tasks.test {
    useJUnitPlatform()
}