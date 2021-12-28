plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.6.0"
    id("uk.co.lukestevens.plugins.release-helper") version "0.1.0"
}

fun RepositoryHandler.githubPackages() = maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/lukecmstevens/packages")
    credentials {
        username = System.getenv("GH_USER")
        password = System.getenv("GH_TOKEN")
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    githubPackages()
}

dependencies {
    implementation("javax.inject:javax.inject:1")
    implementation("uk.co.lukestevens:base-lib:3.0.0-SNAPSHOT")

    implementation("org.hibernate:hibernate-core:5.4.2.Final")
    implementation("com.zaxxer:HikariCP:5.0.0")

    testImplementation("io.mockk:mockk:1.12.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.5.2")
}

group = "uk.co.lukestevens"

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components["java"])
        }
    }
    repositories {
        githubPackages()
    }
}



tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register("generateReadme") {
    doLast {
        val text = File("template.README.md").readText()
            .replace("\${PROJECT_NAME}", rootProject.name)
            .replace("\${PROJECT_VERSION}", rootProject.version.toString())
        File("README.md").writeText(text)
    }
}
