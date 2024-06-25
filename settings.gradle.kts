pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "Fread"
include(":app")
include(":framework")
include(":ActivityPub-Kotlin")
include(":commonbiz")
include(":bizframework:status-provider")
include(":plugins:activitypub-app")
include(":feature:feeds")
include(":feature:explore")
include(":feature:profile")
include(":commonbiz:status-ui")
include(":commonbiz:sharedscreen")
include(":feature:notifications")
include(":plugins:rss")
include(":commonbiz:analytics")
