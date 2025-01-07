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
        mavenLocal()
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "Fread"
include(":ActivityPub-Kotlin")
include(":framework")
include(":bizframework:status-provider")
include(":commonbiz")
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
