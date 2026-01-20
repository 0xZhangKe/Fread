plugins {
    id("fread.project.framework.kmp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.localization"
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

                implementation(compose.components.resources)

                implementation(libs.compose.jb.backhandler)

                implementation(libs.androidx.annotation)

                implementation(libs.arrow.core)

                implementation(libs.jetbrains.lifecycle.runtime)
                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.serialization.kotlinx.json)
                implementation(libs.ktor.client.content.negotiation)

                implementation(libs.imageLoader)
                implementation(libs.okio)
                implementation(libs.ksoup)
                implementation(libs.uri.kmp)
                implementation(libs.bignum)
                implementation(libs.kermit)
                implementation(libs.placeholder.material3)

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
                implementation(compose.uiTooling)
                implementation(compose.preview)
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
        packageOfResClass = "com.zhangke.fread.localization"
        generateResClass = always
    }
}
