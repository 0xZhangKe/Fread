plugins {
    id("utopia.android.library")
    id("kotlin-kapt")
}

android {
    namespace = "com.zhangke.utopia.commonbiz.status.provider"
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
}

dependencies {

    testImplementation("junit:junit:4.+")
    testImplementation(libs.bundles.kotlin)

    implementation(project(path = ":framework"))

    implementation(libs.bundles.kotlin)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.gson)
    implementation(libs.okhttp3)
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.room)
}