plugins {
    id("fread.project.framework.kmp")
}

android {
    namespace = "com.halilibo.richtext.ui"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }
        val commonTest by getting
    }
}