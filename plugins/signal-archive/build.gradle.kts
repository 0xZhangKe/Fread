plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.signal.archive"
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
                implementation(project(path = ":commonbiz:analytics"))

                implementation(compose.components.resources)
                implementation(libs.krouter.runtime)
                implementation(libs.ktor.client.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlin.coroutine.core)
                implementation(libs.imageLoader)
                implementation(libs.jetbrains.lifecycle.viewmodel)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlin.coroutine.test)
            }
        }
    }
    configureCommonMainKsp()
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.signal.archive"
        generateResClass = always
    }
}

dependencies {
    kspAll(libs.androidx.room.compiler)
    kspAll(libs.auto.service.ksp)
    kspAll(libs.krouter.collecting.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
