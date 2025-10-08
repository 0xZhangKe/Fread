import java.io.FileInputStream
import java.util.Properties

plugins {
    id("fread.android.application")
    id("fread.compose.multiplatform")
    id("com.google.devtools.ksp")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.zhangke.fread"

    buildFeatures {
        buildConfig = true
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
        }
        create("release") {
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storePassword = keystoreProperties.getProperty("storePassword")
            keyAlias = keystoreProperties.getProperty("keyAlias")
        }
    }

    defaultConfig {
        applicationId = "com.zhangke.fread"
        versionCode = 107010
        versionName = "1.7.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles("proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
            proguardFiles("proguard-rules.pro")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("debug")
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
    implementation(project(path = ":commonbiz:common"))
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
    if (gradle.extra["enableFirebaseModule"] == true) {
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
    implementation(libs.androidx.appcompat)
//    ksp(libs.krouter.reducing.compiler)
}
