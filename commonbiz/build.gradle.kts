plugins {
    id("utopia.android.library")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.zhangke.utopia.commonbiz"
}

dependencies {

    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.coroutine.core)
    testImplementation(libs.kotlin.coroutine.test)
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")

    implementation(project(path = ":framework"))
    implementation(project(path = ":bizframework:status-provider"))

    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.gson)
}