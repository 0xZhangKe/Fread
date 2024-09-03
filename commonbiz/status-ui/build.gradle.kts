plugins {
    id("fread.project.framework.kmp")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
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

                implementation(libs.bundles.voyager)
                implementation(libs.imageLoader)
                implementation(libs.ktml)
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
                implementation(libs.filt.annotaions)
                implementation(libs.hilt)
                implementation(libs.okhttp3)
                implementation(libs.okhttp3.logging)
                implementation(libs.auto.service.annotations)
                implementation(libs.bundles.androidx.media3)
                implementation(libs.halilibo.richtext)
                implementation(libs.halilibo.richtext.material3)
                implementation(libs.krouter.core)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.filt.compiler)
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.auto.service.ksp)
    add("kspAndroid", libs.krouter.compiler)
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.zhangke.fread.statusui"
        generateResClass = always
    }
}
