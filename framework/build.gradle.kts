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

                implementation(compose.components.resources)

                implementation(libs.androidx.annotation)

                implementation(libs.jetbrains.lifecycle.runtime)
                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)

                implementation(libs.imageLoader)
                implementation(libs.okio)
                implementation(libs.ksoup)
                implementation(libs.uri.kmp)
                implementation(libs.bignum)
                implementation(libs.kermit)
                implementation(libs.placeholder.material3)
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

                implementation(libs.androidx.core.ktx)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.accompanist.permissions)

                implementation(libs.okhttp3)
                implementation(libs.okhttp3.logging)

                implementation(libs.bundles.androidx.media3)
                implementation(libs.krouter.core)

                implementation(libs.composeReorderable)

                implementation(libs.ktml)
                implementation(libs.halilibo.richtext)
                implementation(libs.halilibo.richtext.material3)

                implementation(libs.ktor.client.okhttp)
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
        iosMain {
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.zhangke.fread.framework"
        generateResClass = always
    }
}
