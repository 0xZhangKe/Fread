plugins {
    id("fread.project.feature")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.zhangke.fread.profile"
}

dependencies {

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(path = ":framework"))
    implementation(project(path = ":commonbiz"))
    implementation(project(":commonbiz:analytics"))
    implementation(project(path = ":bizframework:status-provider"))
    implementation(project(path = ":commonbiz:sharedscreen"))
    implementation(project(path = ":commonbiz:status-ui"))
    implementation(project(":commonbiz:analytics"))

    implementation(libs.bundles.kotlin)
    implementation(libs.androidx.core)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.compose.constraintlayout)
    implementation(libs.bundles.androidx.fragment)
    implementation(libs.bundles.androidx.activity)
    implementation(libs.bundles.androidx.preference)
    implementation(libs.bundles.androidx.datastore)
    implementation(libs.bundles.androidx.collection)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.filt.annotaions)
    ksp(libs.filt.compiler)
    implementation(libs.hilt)
    ksp(libs.hilt.compiler)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
    implementation(libs.bundles.voyager)
    implementation(libs.krouter.core)
    ksp(libs.krouter.compiler)
}