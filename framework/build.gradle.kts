plugins {
    id("utopia.android.library")
    id("utopia.android.compose")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.utopia.framework"

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

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
    implementation(libs.bundles.androidx.nav)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.androidx.hilt.nav.compose)
    implementation(libs.androidx.browser)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
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
    api(libs.compose.wheel.picker)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.composeReorderable)
}
