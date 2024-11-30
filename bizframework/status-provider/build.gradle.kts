plugins {
    id("fread.project.framework.kmp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.commonbiz.status.provider"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(libs.bundles.voyager)

                implementation(compose.components.resources)
                implementation(libs.arrow.core)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.androidx.room)

                implementation(libs.ktml)
                implementation(libs.imageLoader)
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

                implementation(libs.halilibo.richtext)
                implementation(libs.halilibo.richtext.material3)

                api(libs.jsoup)
            }
        }
    }
}
