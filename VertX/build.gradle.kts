plugins {
    id("java")
}

group = "my.group"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.vertx:vertx-core:4.5.4")
    implementation("io.vertx:vertx-web:4.5.4")
    implementation("io.vertx:vertx-redis-client:4.5.4")
    implementation("io.vertx:vertx-rx-java3:4.5.4")
    implementation("io.vertx:vertx-rx-java3-gen:4.5.4")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}