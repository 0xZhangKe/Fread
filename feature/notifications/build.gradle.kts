plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
    alias(libs.plugins.room)
}

android {
    namespace = "com.zhangke.fread.feature.notifications"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework"))
                implementation(project(":bizframework:status-provider"))
                implementation(project(":commonbiz:common"))
                implementation(project(":commonbiz:analytics"))
                implementation(project(":commonbiz:sharedscreen"))
                implementation(project(":commonbiz:status-ui"))

                implementation(compose.components.resources)

                implementation(libs.bundles.voyager)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.androidx.paging.common)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.auto.service.annotations)
                implementation(libs.krouter.runtime)
                implementation(libs.androidx.room)
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
    kspAll(libs.auto.service.ksp)
    kspAll(libs.krouter.collecting.compiler)
    kspAll(libs.androidx.room.compiler)
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.feature.notifications"
        generateResClass = always
    }
}

room {
    schemaDirectory("$projectDir/schemas")
}
