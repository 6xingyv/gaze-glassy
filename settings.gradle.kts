rootProject.name = "gaze-glassy"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        providers.gradleProperty("localMavenRepo").orNull?.takeIf { it.isNotBlank() }?.let { localMavenRepo ->
            maven {
                url = uri(localMavenRepo)
                mavenContent {
                    includeGroupAndSubgroups("com.mocharealm")
                }
            }
        }
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":core")
include(":liquid:effect")
include(":liquid:settings:core")
include(":liquid:settings:configurator")
include(":liquid:settings:client")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
