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
                implementation(project(":commonbiz"))
                implementation(project(":commonbiz:analytics"))
                implementation(project(":commonbiz:sharedscreen"))
                implementation(project(":commonbiz:status-ui"))

                implementation(libs.bundles.voyager)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinInject.runtime)
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
                implementation(libs.krouter.core)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.kotlinInject.compiler)
}