import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

buildscript {
    repositories {
        jcenter()
        mavenLocal()
    }

}

val gradleScriptDir by extra("${rootProject.projectDir}/gradle")

tasks.withType(Wrapper::class) {
    gradleVersion = "6.0.1"
}

plugins {
    java
    id("ru.vyarus.quality") version "4.0.0"
    id("com.jfrog.bintray") version "1.8.0"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    id("net.researchgate.release") version "2.8.1"
}

release {
    tagTemplate = "\${version}"
}

configure(listOf(rootProject)) {
    description = "Atlas"
    group = "io.qameta.atlas"
}

val afterReleaseBuild by tasks.existing

configure(subprojects) {
    group = "io.qameta.atlas"
    version = version

    apply(plugin = "java")
    apply(plugin = "maven")
    apply(plugin = "java-library")
    apply(plugin = "ru.vyarus.quality")
    apply(plugin = "io.spring.dependency-management")

    apply(from = "$gradleScriptDir/maven.gradle")
    apply(from = "$gradleScriptDir/bintray.gradle")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    val sourceJar by tasks.creating(Jar::class) {
        from(sourceSets.getByName("main").allSource)
        archiveClassifier.set("sources")
    }

    val javadocJar by tasks.creating(Jar::class) {
        from(tasks.getByName("javadoc"))
        archiveClassifier.set("javadoc")
    }

    tasks.withType(Javadoc::class) {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    artifacts.add("archives", sourceJar)
    artifacts.add("archives", javadocJar)

    val bintrayUpload by tasks.existing
    afterReleaseBuild {
        dependsOn(bintrayUpload)
    }

    configure<DependencyManagementExtension> {
        dependencies {
            dependency("org.apache.commons:commons-lang3:3.7")

            dependency("org.seleniumhq.selenium:selenium-java:3.141.59")
            dependency("io.appium:java-client:6.1.0")
            dependency("io.github.bonigarcia:webdrivermanager:2.1.0")
            dependency("ru.yandex.qatools.matchers:webdriver-matchers:1.4.1")
            dependency("org.awaitility:awaitility:3.1.2")

            dependency("org.slf4j:slf4j-api:1.7.25")
            dependency("org.slf4j:slf4j-simple:1.7.25")

            dependency("org.hamcrest:hamcrest-all:1.3")
            dependency("org.assertj:assertj-core:3.6.2")
            dependency("org.mockito:mockito-core:3.2.4")
            dependency("junit:junit:4.12")
        }
    }

    repositories {
        jcenter()
        mavenLocal()
    }
}
