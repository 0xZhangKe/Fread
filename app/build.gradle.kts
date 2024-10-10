import java.io.FileInputStream
import java.util.Properties

plugins {
    id("fread.android.application")
    id("fread.compose.multiplatform")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystorePropertiesFile: File = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

android {
    namespace = "com.zhangke.fread"

    buildFeatures {
        buildConfig = true
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
//        configurations.all {
//            resolutionStrategy { force support.'core-ktx' }
//        }
        applicationId = "com.zhangke.fread"
        versionCode = 101006
        versionName = "1.1.6"

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
    implementation(project(path = ":commonbiz"))
    implementation(project(path = ":framework"))
    implementation(project(path = ":plugins:activitypub-app"))
    implementation(project(path = ":plugins:rss"))
    implementation(project(path = ":bizframework:status-provider"))
    implementation(project(path = ":commonbiz"))
    implementation(project(path = ":commonbiz:status-ui"))
    implementation(project(path = ":commonbiz:sharedscreen"))
    implementation(project(path = ":feature:feeds"))
    implementation(project(path = ":feature:explore"))
    implementation(project(path = ":feature:profile"))
    implementation(project(path = ":feature:notifications"))
    implementation(project(":commonbiz:analytics"))

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(compose.components.resources)

    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(compose.runtime)
    implementation(compose.ui)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.preview)
    implementation(libs.bundles.androidx.fragment)
    implementation(libs.bundles.androidx.activity)
    implementation(libs.bundles.androidx.preference)
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.androidx.collection)
    implementation(libs.androidx.browser)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.room)
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
    implementation(libs.imageLoader)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
    implementation(libs.androidx.paging.common)
    implementation(libs.bundles.voyager)
    implementation(libs.krouter.runtime)
    ksp(libs.krouter.reducing.compiler)
    implementation(libs.bundles.androidx.media3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.composeReorderable)
    implementation(libs.multiplatformsettings.coroutines)
    implementation(libs.rssparser)

    implementation(libs.bundles.googlePlayReview)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    debugImplementation(libs.composeRuntimeTracing)
}

ksp {
    arg("me.tatarka.inject.generateCompanionExtensions", "true")
}
