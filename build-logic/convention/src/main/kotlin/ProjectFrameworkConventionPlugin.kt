import com.zhangke.fread.compose
import com.zhangke.fread.implementation
import com.zhangke.fread.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectFrameworkConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("fread.android.library")
                apply("fread.compose.multiplatform")
            }
            dependencies.apply {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.materialIconsExtended)
                implementation(compose.material3)

                implementation(compose.uiTooling)
                implementation(compose.preview)

                implementation(libs.findLibrary("kotlinx-datetime").get())
            }
        }
    }
}