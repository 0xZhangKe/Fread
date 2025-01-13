plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread.statusui"
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
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz"))
                implementation(project(":commonbiz:analytics"))
                implementation(project(path = ":bizframework:status-provider"))

                implementation(compose.components.resources)

                implementation(libs.arrow.core)

                implementation(libs.bundles.voyager)
                implementation(libs.androidx.annotation)
                implementation(libs.imageLoader)
                implementation(libs.ktml)

                implementation(libs.krouter.runtime)

                implementation(project(":thirds:halilibo-richtext-ui"))
                implementation(project(":thirds:halilibo-richtext-material3"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        androidMain {
            dependencies {
                implementation(compose.preview)
                implementation(libs.androidx.core.ktx)
                implementation(libs.androidx.appcompat)
                implementation(libs.bundles.androidx.media3)

                implementation(libs.okhttp3)
                implementation(libs.okhttp3.logging)

                implementation(libs.auto.service.annotations)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.auto.service.ksp)
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.zhangke.fread.statusui"
        generateResClass = always
    }
}
