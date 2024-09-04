plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.commonbiz.shared.screen"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(":commonbiz:analytics"))

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.bundles.voyager)

                implementation(libs.kotlinInject.runtime)
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
                implementation(libs.androidx.appcompat)
                implementation(libs.androidx.annotation)
                implementation(libs.androidx.compose.constraintlayout)
                implementation(libs.bundles.androidx.fragment)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.bundles.androidx.preference)
                implementation(libs.bundles.androidx.datastore)
                implementation(libs.bundles.androidx.collection)
                implementation(libs.androidx.browser)

                implementation(libs.androidx.room)
                implementation(libs.androidx.room.ktx)

                implementation(libs.androidx.paging.runtime)
                implementation(libs.androidx.paging.compose)

                implementation(libs.okhttp3)
                implementation(libs.okhttp3.logging)

                implementation(libs.auto.service.annotations)
                implementation(libs.krouter.core)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspAndroid", libs.auto.service.ksp)
    add("kspAndroid", libs.krouter.compiler)
}