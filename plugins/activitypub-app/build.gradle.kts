plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.activitypub.app"
    sourceSets {
        getByName("main") {
            assets.srcDirs("src/androidMain/assets")
            res.srcDirs("src/androidMain/res")
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(path = ":commonbiz:sharedscreen"))
                implementation(project(path = ":commonbiz:analytics"))
                implementation(project(path = ":ActivityPub-Kotlin"))

                implementation(compose.components.resources)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.androidx.constraintlayout.compose.kmp)
                implementation(libs.imageLoader)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.androidx.room)
                implementation(libs.auto.service.annotations)
                implementation(libs.androidx.paging.common)
                implementation(libs.bundles.voyager)
                implementation(libs.leftright)

                implementation(libs.krouter.runtime)

                implementation(libs.firebase.kmp.messaging)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                implementation(libs.androidx.core.ktx)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.androidx.browser)
            }
        }
    }
    configureCommonMainKsp()
}

dependencies {
    kspAll(libs.androidx.room.compiler)
    kspAll(libs.kotlinInject.compiler)
    kspAll(libs.auto.service.ksp)
    kspCommonMainMetadata(libs.krouter.collecting.compiler)
}


compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.activitypub.app"
        generateResClass = always
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
