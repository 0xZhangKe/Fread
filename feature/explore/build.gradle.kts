plugins {
    id("fread.project.feature.kmp")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread.explore"
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

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinInject.runtime)
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
}

dependencies {
    kspAll(libs.krouter.collecting.compiler)
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.explore"
        generateResClass = always
    }
}
