import com.android.build.api.dsl.androidLibrary
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    androidLibrary {
        namespace = "com.mocharealm.gaze.glassy.core"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        minSdk = libs.versions.android.minSdk.get().toInt()

        packaging {
            resources {
                excludes += "/META-INF/{AL2.0,LGPL2.1}"
            }
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        macosArm64(),
        macosX64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "GazeCore"
            isStatic = true
        }
    }

    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    targets.all {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    freeCompilerArgs.addAll(
                        "-Xcontext-parameters",
                        "-Xexpect-actual-classes"
                    )
                }
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
            }
        }

        val skikoMain by creating {
            dependsOn(commonMain.get())
        }

        jvmMain {
            dependsOn(skikoMain)
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        jsMain {
            dependsOn(skikoMain)
        }

        wasmJsMain {
            dependsOn(skikoMain)
        }

        listOf(
            iosArm64Main,
            iosSimulatorArm64Main,
            macosArm64Main,
            macosX64Main
        ).forEach { iosTarget ->
            iosTarget {
                dependsOn(skikoMain)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true
        )
    )

    signAllPublications()

    coordinates("com.mocharealm.gaze", "core", "1.0.0-patch1")

    pom {
        name = "Gaze Core"
        description = "Compose Multiplatform RuntimeShader and RenderEffect abstraction layer"
        inceptionYear = "2025"
        url = "https://mocharealm.com/open-source"
        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
            }
        }
        developers {
            developer {
                id = "6xingyv"
                name = "Simon Scholz"
                url = "https://github.com/6xingyv"
            }
        }
        scm {
            url = "https://github.com/6xingyv/gaze-glassy"
            connection = "scm:git:git://github.com/6xingyv/gaze-glassy.git"
            developerConnection = "scm:git:ssh://git@github.com/6xingyv/gaze-glassy.git"
        }
    }
}
