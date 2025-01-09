plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.rss"
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

                implementation(compose.components.resources)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.androidx.room)
                implementation(libs.kotlinInject.runtime)
                implementation(libs.auto.service.annotations)
                implementation(libs.bundles.voyager)
                implementation(libs.uri.kmp)
                implementation(libs.rssparser)
                implementation(libs.okio)

                implementation(libs.krouter.runtime)
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
        packageOfResClass = "com.zhangke.fread.rss"
        generateResClass = always
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
