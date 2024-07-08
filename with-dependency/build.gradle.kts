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

buildscript {
    dependencies {
        classpath("com.alcosi:dependency-license-page-generator:1.0.0")
    }
}

plugins {
    id("idea")
    id("java-library")
    id("maven-publish")
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.0"
    id("com.github.jk1.dependency-license-report") version "2.8"
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("org.jetbrains.dokka") version "1.9.20"
}

val appName = "commons-library-basic-dependency"
val springVersion = "3.3.1"
val depVersion = "4.0.15"
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

centralPortal {
    pom {
        packaging = "jar"
        name.set(project.name)
        description.set(
            """
 This library is a set of frequently used components and includes (in the future it is planned to split into separate libraries):
- Facilitating synchronization of processes, including those in different threads (com.alcosi.lib.synchronisation)
- Logging of incoming and outgoing requests Http,RabbitMQ (com.alcosi.lib.rabbit,com.alcosi.lib.logging.http,com.alcosi.lib.filters)
- Logging of execution time and errors with annotations (using AspectJ) (com.alcosi.lib.logging.annotations)
- Logging SQL queries/responses and notice/exception (for JDBCTemplate) (com.alcosi.lib.db)
- CORS Filter
- Response caching for incoming Http requests. (com.alcosi.lib.filters)
- Error handling for incoming and outgoing requests Http,RabbitMQ (com.alcosi.lib.rabbit,com.alcosi.lib.logging.http)
- RabbitMQ configuration (com.alcosi.lib.rabbit)
- Facilitating the connection of external JARs to the application. (com.alcosi.lib.utils.ExternalJarLoad)
- Set of serializers for Jackson (com.alcosi.lib.serializers)
- Custom thread pools, including with blocking queue (com.alcosi.lib.executors)
- Swagger and OpenAPI distribution (com.alcosi.lib.doc)
- Load balancer when working with Etherium nodes (com.alcosi.lib.crypto)
- Contract caching for WEB3J (com.alcosi.lib.crypto)
- Automatic registration of frequently used components in Spring (only if available in classpath)
- Interface and wrappers for encryption/decryption (com.alcosi.lib.secured.encrypt)
- Encryption key provider interface and implementations - in env. variable and through http
- Thread context form headers/to headers (com.alcosi.lib.filters,com.alcosi.lib.logging.http)
- Simple authentication (com.alcosi.lib.filters)
- Secured data containers with JSON serialization and log masking (com.alcosi.lib.secured.container,com.alcosi.lib.secured.logging.files,com.alcosi.lib.serializers)
With dependencies
        """,
        )
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
        developers {
            developer {
                id.set("Alcosi")
                name.set("Alcosi Group")
                email.set("info@alcosi.com")
                url.set("alcosi.com")
            }
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
    api("io.github.breninsul:synchronization-starter:1.0.2")
    api("io.github.breninsul:future-starter:1.0.2")
    api("io.github.breninsul:rest-template-logging-interceptor:1.1.0")
    api("io.github.breninsul:okhttp-logging-interceptor:1.0.7")
    api("org.apache.logging.log4j:log4j-api-kotlin:1.4.0")
    api("jakarta.servlet:jakarta.servlet-api:6.0.0")
    api("javax.annotation:javax.annotation-api:1.3.2")
    api("commons-io:commons-io:2.16.1")
    api("org.apache.commons:commons-lang3:3.14.0")
    api("commons-codec:commons-codec:1.17.0")
    api("org.apache.commons:commons-text:1.12.0")
    api("org.bouncycastle:bcprov-jdk18on:1.78.1")
    api("org.postgresql:postgresql:42.7.3")
    api("org.flywaydb:flyway-core:10.14.0")
    api("org.flywaydb:flyway-database-postgresql:10.14.0")
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

kotlin {
    jvmToolchain(javaVersion.majorVersion.toInt())
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
tasks.named("generateLicenseReport") {
    outputs.upToDateWhen { false }
}

tasks.named("generateLicenseReport") {
    outputs.upToDateWhen { false }
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
