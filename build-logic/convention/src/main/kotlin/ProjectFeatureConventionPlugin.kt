import com.android.build.api.variant.LibraryAndroidComponentsExtension
import com.android.build.gradle.LibraryExtension
import com.zhangke.fread.compose
import com.zhangke.fread.configureKotlinAndroid
import com.zhangke.fread.configurePrintApksTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ProjectFeatureConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.serialization")

                apply("fread.compose.multiplatform")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.targetSdk = 34
            }
            extensions.configure<LibraryAndroidComponentsExtension> {
                configurePrintApksTask(this)
            }
            dependencies.apply {
                add("implementation", compose.runtime)
                add("implementation", compose.ui)
                add("implementation", compose.foundation)
                add("implementation", compose.material)
                add("implementation", compose.materialIconsExtended)
                add("implementation", compose.material3)

                add("implementation", compose.uiTooling)
                add("implementation", compose.preview)
            }
        }
    }
}