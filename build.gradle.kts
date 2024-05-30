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
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.kotlin.plugin.spring") version "2.0.0"
    id("com.github.jk1.dependency-license-report") version "2.8"
    id("com.github.ben-manes.versions") version "0.50.0"
    id("org.jetbrains.dokka") version "1.9.20"
}

group = "com.alcosi"
version = "4.0.1"
val appName = "commons-library"

val jacksonVersion = "2.17.1"
val web3jVersion = "4.12.0"
val kotlinVersion = "2.0.0"
val javaVersion = JavaVersion.VERSION_21


java {
    sourceCompatibility = javaVersion
}
java {
    withJavadocJar()
    withSourcesJar()
}
publishing {
    repositories {

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/alcosi/alcosi_commons_library")
            credentials {
                username = "${System.getenv()["GIHUB_PACKAGE_USERNAME"] ?: System.getenv()["GITHUB_PACKAGE_USERNAME"]}"
                password = "${System.getenv()["GIHUB_PACKAGE_TOKEN"] ?: System.getenv()["GITHUB_PACKAGE_TOKEN"]}"
            }
        }

    }
    publications {
        create<MavenPublication>("Lib") {
            from(components["java"])
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



tasks.compileJava {
    dependsOn.add(tasks.processResources)
}
tasks.compileKotlin {
    dependsOn.add(tasks.processResources)
}

repositories {
    mavenCentral()
}

configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
        exclude(module = "logback-classic")
        exclude(module = "spring-boot-starter-logging")
        exclude(module = "spring-boot-starter-tomcat")
    }
}


dependencies {
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("io.github.breninsul:configurable-transaction-template-starter:1.0.2")
    compileOnly("io.github.breninsul:named-limited-virtual-thread-executor:1.0.0")
    compileOnly("io.github.breninsul:jdbc-template-postgresql-types:1.0.8")
    compileOnly("io.github.breninsul:java-timer-scheduler-starter:1.0.3")
    compileOnly("io.github.breninsul:synchronization-starter:1.0.1")
    compileOnly("io.github.breninsul:future-starter:1.0.2")
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("commons-io:commons-io:2.16.1")
    compileOnly("commons-codec:commons-codec:1.16.1")
    compileOnly("org.apache.commons:commons-text:1.12.0")
    compileOnly("org.bouncycastle:bcprov-jdk18on:1.78.1")
    compileOnly("org.postgresql:postgresql:42.7.3")
    compileOnly("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    compileOnly("org.web3j:crypto:$web3jVersion")
    compileOnly("org.web3j:core:$web3jVersion")
    compileOnly("org.jetbrains.kotlin:kotlin-reflect")
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter")
    compileOnly("org.springframework.boot:spring-boot-starter-amqp")
    compileOnly("org.springframework.boot:spring-boot-starter-jdbc")
    compileOnly("org.springframework.boot:spring-boot-starter-aop")
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("org.apache.logging.log4j:log4j-core")
    annotationProcessor("org.apache.logging.log4j:log4j-core")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.8.0")
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
val javadocJar = tasks.named<Jar>("javadocJar") {
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
    renderers = arrayOf(
        JsonGroupedGenerator("group-report.json", onlyOneLicensePerModule = false),
        MDGroupedGenerator("../../DEPENDENCIES.md", onlyOneLicensePerModule = false)
    )
}