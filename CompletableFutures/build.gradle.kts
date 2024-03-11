plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
}

group = "my.group"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(platform("org.apache.logging.log4j:log4j-bom:2.23.1"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.apache.logging.log4j:log4j-to-slf4j")
    runtimeOnly("org.apache.logging.log4j:log4j-core")

    testImplementation("org.mockito:mockito-core:5.11.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}