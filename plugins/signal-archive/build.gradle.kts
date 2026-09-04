plugins {
    id("fread.project.feature.kmp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.signal.archive"
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(path = ":framework"))
                implementation(project(path = ":commonbiz:common"))
                implementation(project(path = ":bizframework:status-provider"))
                implementation(project(path = ":commonbiz:status-ui"))
                implementation(project(path = ":commonbiz:sharedscreen"))
                implementation(project(path = ":commonbiz:analytics"))

                implementation(compose.components.resources)
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

compose {
    resources {
        publicResClass = false
        packageOfResClass = "com.zhangke.fread.signal.archive"
        generateResClass = always
    }
}
