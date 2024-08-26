import java.io.FileInputStream
import java.util.Properties

plugins {
    id("fread.android.application")
    id("com.google.dagger.hilt.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
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
        versionCode = 100091
        versionName = "1.0.91"

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

    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    val composeBom = platform(libs.androidx.compose.bom)
    implementation(composeBom)
    implementation(libs.bundles.androidx.compose.ui)
    implementation(libs.bundles.androidx.compose.foundation)
    implementation(libs.bundles.androidx.compose.material)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.compose.material3)
    implementation(libs.bundles.androidx.viewmodel)
    implementation(libs.bundles.androidx.lifecycle)
    kapt(libs.androidx.lifecycle.compiler)
    implementation(libs.bundles.androidx.fragment)
    implementation(libs.bundles.androidx.activity)
    implementation(libs.bundles.androidx.preference)
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.androidx.collection)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.browser)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.coil.video)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.joda.time)
    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)
    implementation(libs.filt.annotaions)
    ksp(libs.filt.compiler)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.bundles.voyager)
    implementation(libs.krouter.core)
    ksp(libs.krouter.compiler)
    implementation(libs.bundles.androidx.media3)
    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.composeReorderable)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}
