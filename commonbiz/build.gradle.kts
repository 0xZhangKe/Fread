plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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

                implementation(libs.androidx.room)
                implementation(libs.bundles.voyager)
            }
        }
        androidMain {
            dependencies {
                implementation(libs.bundles.androidx.activity)
                implementation(libs.androidx.appcompat)

                implementation(libs.androidx.browser)

                implementation(libs.hilt)
                implementation(libs.krouter.core)
                implementation(libs.filt.annotaions)
            }
        }
        androidUnitTest {
            dependencies {
                implementation(libs.junit)
                implementation(libs.androidx.test.ext.junit)
                implementation(libs.androidx.test.espresso.core)
                implementation(libs.kotlin.coroutine.core)
                implementation(libs.kotlin.coroutine.test)
                implementation(libs.mockk)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.krouter.compiler)
    add("kspAndroid", libs.filt.compiler)
}