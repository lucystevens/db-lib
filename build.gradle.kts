plugins {
    java
    `maven-publish`
    kotlin("jvm") version "1.6.0"
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

    testImplementation("com.h2database:h2:1.4.199")
    testImplementation("org.mockito:mockito-core:3.6.0")
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

/**
 *  Scripts (remove once release-helper is approved via gradle plugin portal)
 */

tasks.register("exportProperties") {
    doLast {
        File(System.getenv("GITHUB_ENV")).bufferedWriter().use {
            it.appendln("PROJECT_VERSION=${project.version}")
            it.appendln("PROJECT_NAME=${project.name}")
        }
    }
}

fun writeVersion(newVersion: String) {
    File(projectDir, "gradle.properties")
        .bufferedWriter().use {
            it.appendln("version=$newVersion")
        }
}

fun removeSuffix(version: String): String = version.substringBefore("-")

tasks.register("finaliseVersion") {
    doLast {
        val currentVersion = project.version.toString()
        val newVersion = removeSuffix(currentVersion)
        if(currentVersion != newVersion) {
            writeVersion(newVersion)
        }
    }
}

tasks.register("bumpVersion") {
    doLast {
        val currentVersion = project.version.toString()
        val finalVersion = removeSuffix(currentVersion)
        val versionParts = finalVersion.split(".")
        if(versionParts.size < 2){
            throw IllegalArgumentException("Version must have at least major and minor identifier")
        }

        // Add major and minor versions
        val newVersion = StringBuilder(versionParts[0])
            .append(".")
            .append(Integer.parseInt(versionParts[1]) + 1)
        for(i in 2 until versionParts.size){
            newVersion.append(".0") // zero all other parts
        }
        newVersion.append("-SNAPSHOT")

        writeVersion(newVersion.toString())
    }
}

tasks.register("generateReadme") {
    doLast {
        val text = File("template.README.md").readText()
            .replace("\${PROJECT_NAME}", rootProject.name)
            .replace("\${PROJECT_VERSION}", rootProject.version.toString())
        File("README.md").writeText(text)
    }
}
