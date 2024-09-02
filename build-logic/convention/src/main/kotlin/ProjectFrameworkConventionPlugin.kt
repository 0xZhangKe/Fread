import com.zhangke.fread.compose
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