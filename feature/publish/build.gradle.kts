plugins {
    id("utopia.android.feature")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.zhangke.utopia.publish"

    // Allow references to generated code
    kapt {
        correctErrorTypes = true
    }
}

dependencies {

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(path = ":framework"))
    implementation(project(path = ":commonbiz"))
    implementation(project(path = ":commonbiz:status-provider"))

    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.multidex)
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
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.filt.annotaions)
    implementation(libs.accompanist.placeholder.material)
    ksp(libs.filt.compiler)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.coil)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.joda.time)
    implementation(libs.auto.service.annotations)
    kapt(libs.auto.service)
    implementation(libs.bundles.voyager)
    implementation(libs.krouter.core)
    ksp(libs.krouter.compiler)
}