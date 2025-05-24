plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.bluesky"
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
                implementation(project(path = ":commonbiz:common"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(path = ":commonbiz:sharedscreen"))
                implementation(project(":commonbiz:analytics"))

                implementation(compose.components.resources)

                implementation(libs.arrow.core)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.client.serialization.kotlinx.json)
                implementation(libs.ktor.client.logging)
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
                api(libs.bluesky)
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
    kspAll(libs.krouter.collecting.compiler)
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.bluesky"
        generateResClass = always
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
