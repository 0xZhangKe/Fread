plugins {
    id("fread.android.library")
    id("com.google.devtools.ksp")
    id("kotlin-parcelize")
}

android {
    namespace = "com.zhangke.fread.commonbiz.status.provider"
}

dependencies {

    testImplementation(libs.mockk)
    testImplementation("junit:junit:4.+")
    testImplementation(libs.bundles.kotlin)

    implementation(project(path = ":framework"))

    implementation(libs.bundles.kotlin)

    implementation(libs.okhttp3)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.room)
    ksp(libs.krouter.compiler)

    implementation(libs.kotlinx.serialization.core)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.ktml)
    implementation(libs.halilibo.richtext)
    implementation(libs.halilibo.richtext.material3)
    api(libs.jsoup)
    implementation(libs.coil)
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)
}