/*
 *
 *  * Copyright (c) 2024 Alcosi Group Ltd. and affiliates.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */

import com.alcosi.gradle.dependency.group.JsonGroupedGenerator
import com.alcosi.gradle.dependency.group.MDGroupedGenerator
import com.github.jk1.license.LicenseReportExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        maven {
            name = "GitHub"
            url = uri("https://maven.pkg.github.com/alcosi/gradle-dependency-license-page-generator")
            credentials {
                username = "${System.getenv()["GIHUB_PACKAGE_USERNAME"] ?: System.getenv()["GITHUB_PACKAGE_USERNAME"]}"
                password = "${System.getenv()["GIHUB_PACKAGE_TOKEN"] ?: System.getenv()["GITHUB_PACKAGE_TOKEN"]}"
            }
        }
    }
    dependencies {
        classpath("com.alcosi:dependency-license-page-generator:1.0.0")
    }
}

plugins {
    id("idea")
    id("java-library")
    id("maven-publish")
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.0"
    id("com.github.jk1.dependency-license-report") version "2.8"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("org.jetbrains.dokka") version "1.9.20"
}

val appName = "commons-library-basic-dependency"
val springVersion = "3.3.0"
val depVersion = "4.0.4"
val kotlinCoroutinesVersion = "1.8.1"

val jacksonVersion = "2.17.1"
val web3jVersion = "4.12.0"
val kotlinVersion = "2.0.0"
val javaVersion = JavaVersion.VERSION_21

group = "com.alcosi"
version = "$springVersion.$depVersion"

java {
    sourceCompatibility = javaVersion
    withJavadocJar()
    withSourcesJar()
}
val repo = "github.com/alcosi/alcosi_commons_library"

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.$repo")
            credentials {
                username = "${System.getenv()["GIHUB_PACKAGE_USERNAME"] ?: System.getenv()["GITHUB_PACKAGE_USERNAME"]}"
                password = "${System.getenv()["GIHUB_PACKAGE_TOKEN"] ?: System.getenv()["GITHUB_PACKAGE_TOKEN"]}"
            }
        }
    }
    publications {
        create<MavenPublication>("Lib") {
            from(components["java"])
            groupId = group.toString()
            artifactId = appName
            version = version
            uri("https://$repo/tree/main/with-dependency")
            pom {
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
            }
        }
    }
}

centralPortal {
    pom {
        packaging = "jar"
        name.set(project.name)
        val repository = "https://$repo"
        url.set(repository)
        licenses {
            license {
                name.set("Apache 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0")
            }
        }
        scm {
            connection.set("scm:$repository.git")
            developerConnection.set("scm:git@$repo.git")
            url.set(repository)
        }
    }
}

tasks.compileJava {
    dependsOn.add(tasks.processResources)
}
tasks.compileKotlin {
    dependsOn.add(tasks.processResources)
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven {
        name = "GitHub"
        url = uri("https://maven.pkg.github.com/alcosi/alcosi_commons_library")
        credentials {
            username = "${System.getenv()["GIHUB_PACKAGE_USERNAME"] ?: System.getenv()["GITHUB_PACKAGE_USERNAME"]}"
            password = "${System.getenv()["GIHUB_PACKAGE_TOKEN"] ?: System.getenv()["GITHUB_PACKAGE_TOKEN"]}"
        }
    }
}

configurations.configureEach {
    exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    exclude(module = "logback-classic")
    exclude(module = "spring-boot-starter-logging")
    exclude(module = "spring-boot-starter-tomcat")
}

dependencies {
    api("com.alcosi:commons-library-logging:$depVersion")
    api("io.github.breninsul:configurable-transaction-template-starter:1.0.2")
    api("io.github.breninsul:named-limited-virtual-thread-executor:1.0.0")
    api("io.github.breninsul:jdbc-template-postgresql-types:1.0.8")
    api("io.github.breninsul:java-timer-scheduler-starter:1.0.3")
    api("io.github.breninsul:synchronization-starter:1.0.1")
    api("io.github.breninsul:future-starter:1.0.2")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
    api("jakarta.servlet:jakarta.servlet-api:6.0.0")
    api("javax.annotation:javax.annotation-api:1.3.2")
    api("commons-io:commons-io:2.16.1")
    api("org.apache.commons:commons-lang3:3.14.0")
    api("commons-codec:commons-codec:1.17.0")
    api("org.apache.commons:commons-text:1.12.0")
    api("org.bouncycastle:bcprov-jdk18on:1.78.1")
    api("org.postgresql:postgresql:42.7.3")
    api("org.flywaydb:flyway-core:10.13.0")
    api("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    api("org.springframework.boot:spring-boot-starter-web:$springVersion")
    api("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    api("org.springframework.boot:spring-boot-starter:$springVersion")
    api("org.springframework.boot:spring-boot-starter-jdbc:$springVersion")
    api("org.springframework.boot:spring-boot-starter-log4j2:$springVersion")
    api("org.springframework.boot:spring-boot-starter-aop:$springVersion")
    api("org.springframework.boot:spring-boot-starter-actuator:$springVersion")
    api("org.springframework.boot:spring-boot-starter-jetty:$springVersion")
    api("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    api("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = javaVersion.majorVersion
    }
}

tasks.withType<Test> {
    jvmArgs("-Xmx1024m")
    useJUnitPlatform()
}
val javadocJar =
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaJavadoc"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
tasks.getByName<Jar>("jar") {
    enabled = true
    archiveClassifier = ""
}

signing {
    useGpgCmd()
}
idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

licenseReport {
    unionParentPomLicenses = false
    outputDir = "$projectDir/reports/license"
    configurations = LicenseReportExtension.ALL
    excludeOwnGroup = false
    excludeBoms = false
    renderers =
        arrayOf(
            JsonGroupedGenerator("group-report.json", onlyOneLicensePerModule = false),
            MDGroupedGenerator("../../DEPENDENCIES.md", onlyOneLicensePerModule = false),
        )
}
