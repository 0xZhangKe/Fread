plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.commonbiz"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":bizframework:status-provider"))

                implementation(libs.bundles.androidx.datastore)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.jetbrains.lifecycle.runtime)
                implementation(libs.jetbrains.lifecycle.viewmodel)
                implementation(libs.jetbrains.lifecycle.viewmodel.compose)

                implementation(libs.kotlinInject.runtime)

                implementation(libs.androidx.room)
                implementation(libs.bundles.voyager)

                implementation(libs.krouter.runtime)
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
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.kotlinInject.compiler)
}