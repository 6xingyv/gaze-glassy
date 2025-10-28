import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    id("com.vanniktech.maven.publish") version "0.34.0"
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
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
            // 'this' 是 KotlinCompilation
            compileTaskProvider.configure {
                // 'this' 是对应的编译任务 (e.g., KotlinCompile)
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
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)
            }
        }

        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.activity.compose)
            }
        }

        val skikoMain by creating {
            dependsOn(commonMain.get())
        }

        jvmMain {
            dependsOn(skikoMain)
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.kotlinx.coroutinesSwing)
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

android {
    namespace = "com.mocharealm.gaze.glassy"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
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

    coordinates("com.mocharealm.gaze", "glassy", "1.0.0")

    pom {
        name = "Gaze Glassy"
        description = "Compose Multiplatform liquid glass effect"
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