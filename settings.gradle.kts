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
        maven("https://jitpack.io")
        maven("https://plugins.gradle.org/m2/")
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        maven("https://oss.sonatype.org/content/repositories/snapshots/")
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
include(":plugins:bluesky")
