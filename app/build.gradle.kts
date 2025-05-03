plugins {
    id("fread.android.application")
    id("fread.compose.multiplatform")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
//        configurations.all {
//            resolutionStrategy { force support.'core-ktx' }
//        }
        applicationId = "com.zhangke.fread"
        versionCode = 103010
        versionName = "1.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    bundle {
        language {
            enableSplit = false
        }
    }
}

dependencies {
    implementation(project(path = ":framework"))
    implementation(project(path = ":bizframework:status-provider"))
    implementation(project(path = ":commonbiz"))
    implementation(project(path = ":commonbiz:analytics"))
    implementation(project(path = ":commonbiz:status-ui"))
    implementation(project(path = ":commonbiz:sharedscreen"))
    implementation(project(path = ":feature:feeds"))
    implementation(project(path = ":feature:explore"))
    implementation(project(path = ":feature:profile"))
    implementation(project(path = ":feature:notifications"))
    implementation(project(path = ":plugins:activitypub-app"))
    implementation(project(path = ":plugins:rss"))
    implementation(project(path = ":plugins:bluesky"))
    if (File(project.rootDir, "plugins/fread-firebase").exists()) {
        implementation(project(path = ":plugins:fread-firebase"))
    }
    implementation(project(path = ":app-hosting"))

    implementation(compose.material3)
    implementation(compose.components.resources)

    implementation(libs.bundles.androidx.activity)
    implementation(libs.bundles.voyager)
    implementation(libs.imageLoader)

    implementation(libs.krouter.runtime)

    implementation(libs.bundles.androidx.media3)
}
