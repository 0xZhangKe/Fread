import com.zhangke.fread.compose
import com.zhangke.fread.kotlinMultiplatform
import com.zhangke.fread.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

class ProjectFrameworkKmpConventionPlugin: Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("fread.kmp.library")
                apply("fread.compose.multiplatform")
            }
            kotlinMultiplatform {
                sourceSets.apply {
                    commonMain {
                        dependencies {
                            implementation(compose.runtime)
                            implementation(compose.ui)
                            implementation(compose.foundation)
                            implementation(compose.material)
                            implementation(compose.materialIconsExtended)
                            implementation(compose.material3)

                            implementation(libs.findLibrary("kotlinx-datetime").get())
                        }
                    }
                    targets.configureEach {
                        val isAndroidTarget = platformType == KotlinPlatformType.androidJvm
                        compilations.configureEach {
                            compileTaskProvider.configure {
                                compilerOptions {
                                    if (isAndroidTarget) {
                                        freeCompilerArgs.addAll(
                                            "-P",
                                            "plugin:org.jetbrains.kotlin.parcelize:additionalAnnotation=com.zhangke.framework.utils.Parcelize",
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}