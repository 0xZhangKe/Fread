plugins {
    id("fread.project.framework.kmp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.framework"
    sourceSets {
        getByName("main") {
            res.srcDirs("src/commonMain/res")
            resources.srcDirs("src/commonMain/resources")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.bundles.voyager)

                implementation(libs.androidx.annotation)

                implementation(libs.jetbrains.lifecycle.runtime)
                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                implementation(compose.uiTooling)
                implementation(compose.preview)

                implementation(libs.androidx.core)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.compose.constraintlayout)
                implementation(libs.bundles.androidx.fragment)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.accompanist.placeholder.material)
                implementation(libs.accompanist.permissions)

                implementation(libs.coil)
                implementation(libs.coil.compose)
                implementation(libs.coil.gif)
                implementation(libs.okhttp3)
                implementation(libs.okhttp3.logging)

                implementation(libs.bundles.androidx.media3)
                implementation(libs.krouter.core)
                api(libs.compose.wheel.picker)

                implementation(libs.composeReorderable)

                implementation(libs.ktml)
                implementation(libs.halilibo.richtext)
                implementation(libs.halilibo.richtext.material3)
            }
        }
        androidUnitTest {
            dependencies {
            }
        }
        androidInstrumentedTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.espresso.core)
            }
        }
    }
}
