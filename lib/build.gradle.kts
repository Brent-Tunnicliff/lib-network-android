// Copyright © 2024 Brent Tunnicliff <brent@tunnicliff.dev>

plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "dev.tunnicliff.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        buildConfig = true
    }
}

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                from(components["release"])
                groupId = "dev.tunnicliff"
                artifactId = "lib-network-android"
                version = "0.1.0-alpha.8"

                pom {
                    packaging = "aar"
                    name.set("lib-network-android")
                    description.set("lib-network-android: Library for handling network API calls.")
                    url.set("https://github.com/Brent-Tunnicliff/lib-network-android")
                    inceptionYear.set("2024")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }

                    developers {
                        developer {
                            id.set("brent")
                            name.set("Brent Tunnicliff")
                            email.set("brent@tunnicliff.dev")
                        }
                    }

                    scm {
                        connection.set("scm:git:https://github.com/Brent-Tunnicliff/lib-network-android.git")
                        developerConnection.set("scm:git:ssh://git@github.com:Brent-Tunnicliff/lib-network-android.git")
                        url.set("https://github.com/Brent-Tunnicliff/lib-network-android")
                    }
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.dev.tunnicliff.lib.container.android)
    implementation(libs.dev.tunnicliff.lib.logging.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}