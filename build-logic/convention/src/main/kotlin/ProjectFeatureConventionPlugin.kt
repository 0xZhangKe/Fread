import org.gradle.api.Plugin
import org.gradle.api.Project

class ProjectFeatureConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("fread.project.framework")
            }
        }
    }
}