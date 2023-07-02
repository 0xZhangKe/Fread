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
rootProject.name = "Utopia"
include(":app")
include(":framework")
include(":ActivityPub-Kotlin")
include(":commonbiz")
include(":commonbiz:status-provider")
include(":commonbiz:activitypub-app")
include(":feature:feeds")
include(":feature:explore")
include(":feature:publish")
include(":feature:profile")
