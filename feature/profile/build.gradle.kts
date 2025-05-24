plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread.profile"
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

                implementation(libs.arrow.core)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.androidx.paging.common)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.auto.service.annotations)
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
                implementation(libs.bundles.androidx.activity)
                implementation(libs.androidx.browser)
            }
        }
    }
    configureCommonMainKsp()
}

dependencies {
    kspAll(libs.auto.service.ksp)
    kspAll(libs.kotlinInject.compiler)
    kspAll(libs.krouter.collecting.compiler)
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.feature.profile"
        generateResClass = always
    }
}
