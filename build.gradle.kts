import com.alcosi.gradle.dependency.group.JsonGroupedGenerator
import com.alcosi.gradle.dependency.group.MDGroupedGenerator
import com.github.jk1.license.LicenseReportExtension
import org.jetbrains.kotlin.gradle.utils.extendsFrom

buildscript {
    dependencies {
        classpath("com.alcosi:dependency-license-page-generator:1.0.0")
    }
}
plugins {
    val kotlinVersion = "2.1.0"
    id("idea")
    id("java-library")
    id("maven-publish")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.4"
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion
    id("com.github.jk1.dependency-license-report") version "2.9"
    id("com.github.ben-manes.versions") version "0.51.0"
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.jetbrains.kotlin.kapt") version kotlinVersion
}

group = "com.alcosi"
version = "5.1.1"
val appName = "commons-library"

val jacksonVersion = "2.18.2"
val web3jVersion = "4.12.2"
val kotlinVersion = "2.1.0"
val javaVersion = JavaVersion.VERSION_21

java {
    sourceCompatibility = javaVersion
}
java {
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
Without dependencies
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
    compileOnly("io.github.breninsul:named-limited-virtual-thread-executor:1.0.2")
    compileOnly("io.github.breninsul:jdbc-template-postgresql-types:1.0.8")
    compileOnly("io.github.breninsul:java-timer-scheduler-starter:1.0.3")
    compileOnly("io.github.breninsul:synchronization-starter:1.0.2")
    compileOnly("io.github.breninsul:future-starter:1.0.2")
    compileOnly("io.github.breninsul:rest-template-logging-interceptor:1.3.0")
    compileOnly("io.github.breninsul:okhttp-logging-interceptor:1.2.0")
    compileOnly("io.github.breninsul:servlet-logging-starter:1.1.0")
    compileOnly("org.apache.commons:commons-lang3:3.17.0")
    compileOnly("commons-codec:commons-codec:1.17.1")
    compileOnly("org.apache.commons:commons-text:1.12.0")
    compileOnly("commons-io:commons-io:2.18.0")
    compileOnly("com.squareup.okhttp3:okhttp:4.12.0")
    compileOnly("org.bouncycastle:bcprov-jdk18on:1.79")
    compileOnly("org.postgresql:postgresql:42.7.4")
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
    compileOnly("org.springframework.boot:spring-boot-starter-actuator")
    compileOnly("org.springframework.security:spring-security-rsa:1.1.3")
    compileOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    compileOnly("org.apache.logging.log4j:log4j-core")
    annotationProcessor("org.apache.logging.log4j:log4j-core")
    kapt("org.apache.logging.log4j:log4j-core")

    kapt("org.springframework.boot:spring-boot-autoconfigure-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core:5.8.0")
}

configurations {
    testImplementation.extendsFrom(compileOnly)
}

kotlin {
    jvmToolchain(javaVersion.majorVersion.toInt())
}

tasks.withType<Test> {
    jvmArgs("-Xmx1024m", "--add-exports", "java.base/sun.security.rsa=ALL-UNNAMED")
    useJUnitPlatform()
}
val javadocJar =
    tasks.named<Jar>("javadocJar") {
        from(tasks.named("dokkaJavadoc"))
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

tasks.jar {
    enabled = true
    archiveClassifier.set("")
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
