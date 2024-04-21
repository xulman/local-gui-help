plugins {
    embeddedKotlin("jvm")
    publish
}

repositories {
    mavenCentral()
    maven("https://maven.scijava.org/content/groups/public")
}

group = "sc.fiji"
version = "0.0.1"

dependencies {
    implementation(platform("org.scijava:pom-scijava:37.0.0"))
}