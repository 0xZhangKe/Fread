import org.gradle.kotlin.dsl.DependencyHandlerScope

fun DependencyHandlerScope.kspAll(dependencyNotation: Any) {
    add("kspAndroid", dependencyNotation)
    add("kspIosSimulatorArm64", dependencyNotation)
    add("kspIosX64", dependencyNotation)
    add("kspIosArm64", dependencyNotation)
}
