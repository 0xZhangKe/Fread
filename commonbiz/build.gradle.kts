plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.commonbiz"
    sourceSets {
        getByName("main") {
            res.srcDirs("src/androidMain/res")
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

kotlin {
    sourceSets {
        all {
            languageSettings {
                optIn("com.russhwolf.settings.ExperimentalSettingsApi")
            }
        }
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":bizframework:status-provider"))

                implementation(compose.components.resources)

                implementation(libs.bundles.androidx.datastore)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.jetbrains.lifecycle.runtime)
                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.jetbrains.lifecycle.viewmodel.compose)

                implementation(libs.kotlinInject.runtime)

                implementation(libs.androidx.room)
                implementation(libs.bundles.voyager)
                implementation(libs.uri.kmp)

                implementation(libs.krouter.runtime)

                implementation(libs.multiplatformsettings.core)
                implementation(libs.multiplatformsettings.coroutines)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization.kotlinx.json)
            }
        }
        commonTest {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":bizframework:status-provider"))

                implementation(kotlin("test"))

                implementation(libs.kotlin.coroutine.core)
                implementation(libs.kotlin.coroutine.test)
                implementation(libs.kotlinx.datetime)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.bundles.androidx.activity)
                implementation(libs.androidx.appcompat)

                implementation(libs.androidx.browser)

                implementation(libs.bundles.googlePlayReview)

                implementation(libs.multiplatformsettings.datastore)
            }
        }
        iosMain {
            dependencies {
                implementation(libs.androidx.sqlite.bundled)
            }
        }
    }
}

dependencies {
    kspAll(libs.androidx.room.compiler)
    kspAll(libs.kotlinInject.compiler)
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.zhangke.fread.commonbiz"
        generateResClass = always
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}
