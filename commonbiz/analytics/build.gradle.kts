plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
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

                implementation(libs.firebase.kmp.analytics)
                implementation(libs.firebase.kmp.crashlytics)
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
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.auto.service.ksp)
}