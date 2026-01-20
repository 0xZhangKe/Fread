plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.analytics"
    sourceSets {
        getByName("main") {
            res.srcDirs("src/androidMain/res")
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz:common"))

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
                implementation(libs.androidx.core.ktx)
                implementation(libs.auto.service.annotations)
            }
        }
    }
    configureCommonMainKsp()
}

dependencies {
    kspAll(libs.krouter.collecting.compiler)
}