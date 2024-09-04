import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectFeatureKmpConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("fread.project.framework.kmp")
            }
        }
    }
}