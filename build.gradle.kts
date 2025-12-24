import com.vanniktech.maven.publish.MavenPublishBaseExtension

plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("org.allaymc.gradle.plugin") version "0.2.1"
}

group = "org.allaymc"
description = "TemplateWorld is a plugin to make it able to create a runtime-only world based on a 'template world'"
version = "0.1.0"

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        configureEach {
            options.isFork = true
        }
    }

    // We already have sources jar, so no need to build Javadoc, which would cause a lot of warnings
    withType<Javadoc> {
        enabled = false
    }

    withType<Copy> {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

allay {
    api = "0.19.0"

    plugin {
        entrance = "org.allaymc.templateworld.TemplateWorld"
        authors += "daoge_cmd"
        website = "https://github.com/AllayMC/TemplateWorld"
    }
}

dependencies {
    compileOnly(group = "org.projectlombok", name = "lombok", version = "1.18.34")
    annotationProcessor(group = "org.projectlombok", name = "lombok", version = "1.18.34")
}

configure<MavenPublishBaseExtension> {
    publishToMavenCentral()
    signAllPublications()

    coordinates(project.group.toString(), "template-world", project.version.toString())

    pom {
        name.set(project.name)
        description.set("TemplateWorld is a plugin to make it able to create a runtime-only world based on a 'template world'")
        inceptionYear.set("2025")
        url.set("https://github.com/AllayMC/TemplateWorld")

        scm {
            connection.set("scm:git:git://github.com/AllayMC/TemplateWorld.git")
            developerConnection.set("scm:git:ssh://github.com/AllayMC/TemplateWorld.git")
            url.set("https://github.com/AllayMC/TemplateWorld")
        }

        licenses {
            license {
                name.set("LGPL 3.0")
                url.set("https://www.gnu.org/licenses/lgpl-3.0.en.html")
            }
        }

        developers {
            developer {
                name.set("AllayMC Team")
                organization.set("AllayMC")
                organizationUrl.set("https://github.com/AllayMC")
            }
        }
    }
}
