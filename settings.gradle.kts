pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
    }
}
rootProject.name = "Fread"

val moduleExists = File("./plugins/fread-firebase").exists()
val disableFirebase = gradle.startParameter.projectProperties["disableFirebase"]
val enableFirebaseModule = moduleExists && disableFirebase != "true"
gradle.extra["enableFirebaseModule"] = enableFirebaseModule
println("--------disableFirebase:$disableFirebase, moduleExists: $moduleExists------------- enableFirebaseModule: ${gradle.extra["enableFirebaseModule"]}")

include(":framework")
include(":bizframework:status-provider")
include(":commonbiz:common")
include(":commonbiz:analytics")
include(":commonbiz:status-ui")
include(":commonbiz:sharedscreen")
include(":feature:explore")
include(":feature:feeds")
include(":feature:notifications")
include(":feature:profile")
include(":plugins:rss")
include(":plugins:activitypub-app")
include(":app-hosting")
include(":app")
include(":thirds:halilibo-richtext-ui")
include(":thirds:halilibo-richtext-material3")
include(":plugins:bluesky")
if (enableFirebaseModule) {
    include(":plugins:fread-firebase")
}
