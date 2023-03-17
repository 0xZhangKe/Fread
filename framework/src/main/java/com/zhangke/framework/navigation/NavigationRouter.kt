package com.zhangke.framework.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.navArgument

class NavigationRouter<T : NavigationArgument>(
    val route: String,
    val argument: T,
)

fun <T : NavigationArgument> navigationRouter(
    path: String,
    argument: T,
): NavigationRouter<T> {
    val route = argument.buildRoute(path)
    return NavigationRouter(route, argument)
}

abstract class NavigationArgument {

    abstract val arguments: List<NamedNavArgument>

    infix fun String.argumentTo(builder: NavArgumentBuilder.() -> Unit): NamedNavArgument {
        return navArgument(this, builder)
    }

    internal fun buildRoute(path: String): String {
        val builder = StringBuilder()
        builder.append(path)
        builder.append('?')
        val argumentNames = arguments.map { it.name }
        argumentNames.forEachIndexed { index, arg ->
            builder.append(arg)
            builder.append('=')
            builder.append('{')
            builder.append(arg)
            builder.append('}')
            if (index < argumentNames.size - 1) {
                builder.append('&')
            }
        }
        return builder.toString()
    }
}