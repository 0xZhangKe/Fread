# Convention Plugins

The `build-logic` folder defines project-specific convention plugins, used to keep a single
source of truth for common module configurations.

This approach is heavily based on
[https://developer.squareup.com/blog/herding-elephants/](https://developer.squareup.com/blog/herding-elephants/)
and
[https://github.com/jjohannes/idiomatic-gradle](https://github.com/jjohannes/idiomatic-gradle).

By setting up convention plugins in `build-logic`, we can avoid duplicated build script setup,
messy `subproject` configurations, without the pitfalls of the `buildSrc` directory.

`build-logic` is an included build, as configured in the root
[`settings.gradle.kts`](../settings.gradle.kts).

## Android toolchain

The project uses AGP 9.1.1, Gradle 9.4.1, and Android compile SDK 37 for Compose
Multiplatform 1.13. Run Gradle with JDK 17 or newer. The app's target SDK remains 36.

The root `gradle.properties` temporarily disables built-in Kotlin and the new Android
DSL so the existing `kotlin-android` and KMP `androidTarget()` convention plugins can
run on AGP 9. Before upgrading to AGP 10, migrate the app to built-in Kotlin and the
shared modules to `com.android.kotlin.multiplatform.library`, then remove both flags.

To verify Android compilation and release shrinking without Firebase:

```shell
./gradlew :app:compileDebugKotlin :app:minifyReleaseWithR8 -PdisableFirebase=true
```

## Module conventions

Inside `build-logic` is a `convention` module, which defines a set of plugins that all normal
modules can use to configure themselves.

`build-logic` also includes a set of `Kotlin` files used to share logic between plugins themselves,
which is most useful for configuring Android components (libraries vs applications) with shared
code.

These plugins are *additive* and *composable*, and try to only accomplish a single responsibility.
Modules can then pick and choose the configurations they need.
If there is one-off logic for a module without shared code, it's preferable to define that directly
in the module's `build.gradle`, as opposed to creating a convention plugin with module-specific
setup.

Current list of convention plugins:

- [`nowinandroid.android.application`](convention/src/main/kotlin/AndroidApplicationConventionPlugin.kt),
  [`nowinandroid.android.library`](convention/src/main/kotlin/AndroidLibraryConventionPlugin.kt),
  [`nowinandroid.android.test`](convention/src/main/kotlin/AndroidTestConventionPlugin.kt):
  Configures common Android and Kotlin options.
- [`nowinandroid.android.application.compose`](convention/src/main/kotlin/AndroidApplicationComposeConventionPlugin.kt),
  [`nowinandroid.android.library.compose`](convention/src/main/kotlin/AndroidLibraryComposeConventionPlugin.kt):
  Configures Jetpack Compose options
