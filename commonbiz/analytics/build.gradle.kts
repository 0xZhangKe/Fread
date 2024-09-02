plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    // id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.analytics"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz"))

                implementation(libs.bundles.voyager)
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
                implementation(libs.auto.service.annotations)
                implementation(libs.hilt)
                implementation(libs.firebase.analytics)
                implementation(libs.firebase.crashlytics)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.auto.service.ksp)
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.filt.compiler)

    implementation(platform(libs.firebase.bom))
}