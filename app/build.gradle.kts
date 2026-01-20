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
        versionCode = 107030
        versionName = "1.7.30"

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
    implementation(libs.imageLoader)

    implementation(libs.krouter.runtime)

    implementation(libs.bundles.androidx.media3)
    implementation(libs.androidx.appcompat)
    implementation(libs.bundles.androidx.nav3)
//    ksp(libs.krouter.reducing.compiler)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
}

listOf("assembleRelease", "assembleDebug").forEach { taskName ->
    tasks.whenTaskAdded {
        if (name == taskName) {
            doLast {
                val buildType = if (taskName.contains("Release")) "release" else "debug"
                val apkDir = layout.buildDirectory.dir("outputs/apk/$buildType").get().asFile
                val apkFile = apkDir.listFiles()?.firstOrNull { it.name.endsWith(".apk") }
                if (apkFile != null) {
                    val versionCode = android.defaultConfig.versionCode
                    val enableFirebaseModule = gradle.extra["enableFirebaseModule"] == true
                    val renamedApkName = if (enableFirebaseModule) {
                        "fread-$versionCode-$buildType.apk"
                    } else {
                        "fread-$versionCode-fdroid.apk"
                    }
                    val newFile = File(apkDir, renamedApkName)
                    apkFile.renameTo(newFile)
                    println(">>> APK renamed to ${newFile.absolutePath}")
                }
            }
        }
    }
}
