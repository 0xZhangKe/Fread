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
                implementation(project(path = ":commonbiz:common"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(":commonbiz:analytics"))

                implementation(compose.components.resources)

                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)

                implementation(libs.jetbrains.lifecycle.viewmodel)

                implementation(libs.bundles.voyager)

                implementation(libs.kotlinInject.runtime)
                implementation(libs.imageLoader)

                implementation(libs.krouter.runtime)

                implementation(libs.androidx.paging.common)
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
                implementation(libs.bundles.androidx.fragment)
                implementation(libs.bundles.androidx.activity)
                implementation(libs.bundles.androidx.preference)
                implementation(libs.bundles.androidx.datastore)
                implementation(libs.bundles.androidx.collection)
                implementation(libs.androidx.browser)

                implementation(libs.auto.service.annotations)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspAndroid", libs.kotlinInject.compiler)
    add("kspAndroid", libs.auto.service.ksp)
    kspCommonMainMetadata(libs.krouter.collecting.compiler)
}

compose {
    resources {
        publicResClass = true
        packageOfResClass = "com.zhangke.fread.commonbiz.shared.screen"
        generateResClass = always
    }
}