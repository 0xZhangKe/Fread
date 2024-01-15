package com.zhangke.framework.voyager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.lifecycle.NavigatorDisposable
import cafe.adriel.voyager.navigator.lifecycle.NavigatorLifecycleStore

@OptIn(ExperimentalVoyagerApi::class)
val Navigator.navigationResult: VoyagerResultExtension
    @Composable get() = remember {
        NavigatorLifecycleStore.get(this) {
            VoyagerResultExtension(this)
        }
    }

class VoyagerResultExtension(
    private val navigator: Navigator
) : NavigatorDisposable {
    private val results = mutableStateMapOf<String, Any?>()

    override fun onDispose(navigator: Navigator) {
        // not used
    }

    public fun popWithResult(result: Any? = null) {
        val currentScreen = navigator.lastItem
        results[currentScreen.key] = result
        navigator.pop()
    }

    public fun clearResults() {
        results.clear()
    }

    public fun popUntilWithResult(predicate: (Screen) -> Boolean, result: Any? = null) {
        val currentScreen = navigator.lastItem
        results[currentScreen.key] = result
        navigator.popUntil(predicate)
    }

    @Composable
    public fun <T> getResult(screenKey: String): State<T?> {
        val log = results.keys.joinToString(", ") { key ->
            "$key:${results[key]}"
        }
        val result = results[screenKey] as? T
        val resultState = remember(screenKey, result) {
            derivedStateOf {
                results.remove(screenKey)
                result
            }
        }
        return resultState
    }
}
