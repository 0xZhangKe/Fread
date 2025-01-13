plugins {
    id("fread.project.framework.kmp")
}

android {
    namespace = "com.halilibo.richtext.material3"
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)

                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)

                api(project(":thirds:halilibo-richtext-ui"))
            }
        }
        val commonTest by getting
    }
}