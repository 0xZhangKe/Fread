plugins {
    id("fread.project.framework")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.framework"

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
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.bundles.androidx.viewmodel)
    implementation(libs.bundles.androidx.lifecycle)
    implementation(libs.bundles.androidx.fragment)
    implementation(libs.bundles.androidx.activity)
    implementation(libs.bundles.androidx.preference)
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.androidx.collection)
    implementation(libs.androidx.browser)
    implementation(libs.accompanist.placeholder.material)
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
    implementation(libs.filt.annotaions)
    ksp(libs.filt.compiler)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.bundles.voyager)
    implementation(libs.bundles.androidx.media3)
    implementation(libs.krouter.core)
    api(libs.compose.wheel.picker)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.composeReorderable)
    implementation(libs.ktml)
    implementation(libs.halilibo.richtext)
    implementation(libs.halilibo.richtext.material3)
}
