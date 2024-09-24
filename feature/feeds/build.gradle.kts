plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread.feeds"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":framework"))
                implementation(project(":bizframework:status-provider"))
                implementation(project(":commonbiz"))
                implementation(project(":commonbiz:analytics"))
                implementation(project(":commonbiz:sharedscreen"))
                implementation(project(":commonbiz:status-ui"))

                implementation(compose.components.resources)

                implementation(libs.bundles.voyager)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.krouter.runtime)
                implementation(libs.androidx.room)
                implementation(libs.androidx.constraintlayout.compose.kmp)
                implementation(libs.auto.service.annotations)

                implementation(libs.androidx.paging.common)
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
                implementation(libs.composeReorderable)
            }
        }
    }
}

dependencies {
    kspAll(libs.androidx.room.compiler)
    kspAll(libs.auto.service.ksp)
    kspAll(libs.kotlinInject.compiler)
    kspAll(libs.krouter.collecting.compiler)
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.feeds"
        generateResClass = always
    }
}
