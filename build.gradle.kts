import org.gradle.buildinit.plugins.WrapperPlugin
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningPlugin
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.kt3k.gradle.plugin.CoverallsPlugin

buildscript {
    val extra = project.extensions.extraProperties
    extra["kotlinVersion"] = "1.1-M01"
    extra["coverallsVersion"] = "2.6.3"
    extra["dokkaVersion"] = "0.9.9"


    repositories {
        mavenCentral()
        maven { setUrl("http://dl.bintray.com/kotlin/kotlin-eap-1.1") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlinVersion"]}")
        classpath("org.kt3k.gradle.plugin:coveralls-gradle-plugin:${extra["coverallsVersion"]}")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:${extra["dokkaVersion"]}")
    }
}

group = "com.jenjinstudios"
version = "0.0.0-SNAPSHOT"

val sonatypeUsername by project
val sonatypePassword by project

extra["groovyVersion"] = "2.3.11"
extra["slf4jVersion"] = "1.7.21"
extra["logbackVersion"] = "1.+"
extra["spockVersion"] = "1.0-groovy-2.4"
extra["sonatypeUsername"] = sonatypeUsername
extra["sonatypePassword"] = sonatypePassword

apply {
    plugin("kotlin")
    plugin<GroovyPlugin>()
    plugin<JavaPlugin>()
    plugin<MavenPlugin>()
    plugin<SigningPlugin>()
    plugin<WrapperPlugin>()
    plugin<CoverallsPlugin>()
    plugin<DokkaPlugin>()
}

configure<JavaPluginConvention> {
    setSourceCompatibility(1.6)
    setTargetCompatibility(1.6)
}

repositories {
    mavenCentral()
}

dependencies {
    compile("org.jetbrains.kotlin:kotlin-stdlib:${extra["kotlinVersion"]}") // Kotlin Standard Library
    compile("org.slf4j:slf4j-api:${extra["slf4jVersion"]}") // SLF4J

    testCompile("org.codehaus.groovy:groovy-all:${extra["groovyVersion"]}")
    testCompile("ch.qos.logback:logback-classic:${extra["logbackVersion"]}")
    testCompile("org.spockframework:spock-core:${extra["spockVersion"]}")
}

val dokkaJar = task<Jar>("dokkaJar") {
    dependsOn("dokka")
    classifier = "javadoc"
    from((tasks.getByName("dokka") as DokkaTask).outputDirectory)
}

val sourcesJar = task<Jar>("sourcesJar") {
    classifier = "sources"
    from(the<JavaPluginConvention>().sourceSets.getByName("main").allSource)
}

artifacts.add("archives", dokkaJar)
artifacts.add("archives", sourcesJar)

applyFrom("gradle/reporting.gradle.kts")
applyFrom("gradle/deployment.gradle")
