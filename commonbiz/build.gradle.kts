plugins {
    id("fread.project.framework")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.commonbiz"
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
    implementation(libs.bundles.androidx.preference)
    implementation(libs.bundles.androidx.datastore)

    implementation(libs.androidx.browser)

    implementation(libs.bundles.androidx.activity)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.bundles.voyager)

    implementation(libs.krouter.core)
    ksp(libs.krouter.compiler)

    implementation(libs.filt.annotaions)
    ksp(libs.filt.compiler)

    implementation(libs.bundles.googlePlayReview)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)
}