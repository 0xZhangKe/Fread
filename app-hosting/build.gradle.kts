plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.hosting"
}

kotlin {
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "FreadKit"
            isStatic = true
        }
    }
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:common"))
                implementation(project(path = ":commonbiz:analytics"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(path = ":commonbiz:sharedscreen"))
                implementation(project(path = ":feature:feeds"))
                implementation(project(path = ":feature:explore"))
                implementation(project(path = ":feature:profile"))
                implementation(project(path = ":feature:notifications"))
                implementation(project(path = ":plugins:activitypub-app"))
                implementation(project(path = ":plugins:rss"))
                implementation(project(path = ":plugins:bluesky"))
                if (gradle.extra["enableFirebaseModule"] == true) {
                    implementation(project(path = ":plugins:fread-firebase"))
                }

                implementation(compose.components.resources)

                implementation(libs.bundles.voyager)
                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.krouter.runtime)
                implementation(libs.imageLoader)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.multiplatformsettings.coroutines)
                implementation(libs.rssparser)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                implementation(compose.preview)

                implementation(libs.androidx.core.ktx)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.bundles.androidx.datastore)
            }
        }
    }
    configureCommonMainKsp()
}

dependencies {
    kspAll(libs.kotlinInject.compiler)
    kspAll(libs.krouter.reducing.compiler)
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.hosting"
        generateResClass = always
    }
}
