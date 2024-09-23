plugins {
    id("fread.project.feature")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.zhangke.fread.feeds"
}

dependencies {

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(project(":framework"))
    implementation(project(":bizframework:status-provider"))
    implementation(project(":commonbiz"))
    implementation(project(":commonbiz:analytics"))
    implementation(project(":commonbiz:sharedscreen"))
    implementation(project(":commonbiz:status-ui"))

    implementation(compose.components.resources)

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
    ksp(libs.androidx.room.compiler)
    implementation(libs.kotlinInject.runtime)
    ksp(libs.kotlinInject.compiler)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)
    implementation(libs.auto.service.annotations)
    ksp(libs.auto.service.ksp)
    implementation(libs.androidx.paging.runtime)
    implementation(libs.androidx.paging.compose)
    implementation(libs.bundles.voyager)
    implementation(libs.krouter.runtime)
    ksp(libs.krouter.collecting.compiler)
    implementation(libs.composeReorderable)
}