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
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        maven {
            url = uri("file:///D:/Android/Maven")
            mavenContent {
                includeGroupAndSubgroups("com.mocharealm")
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