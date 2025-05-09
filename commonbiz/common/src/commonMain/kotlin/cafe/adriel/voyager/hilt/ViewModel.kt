package cafe.adriel.voyager.hilt

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import cafe.adriel.voyager.core.screen.Screen
import com.zhangke.fread.common.di.ViewModelCreator
import com.zhangke.fread.common.di.ViewModelFactory
import com.zhangke.fread.common.di.ViewModelKey
import kotlin.reflect.KClass

val LocalViewModelProviderFactory = staticCompositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelProvider.Factory was provided via LocalViewModelProviderFactory")
}

@Composable
public inline fun <reified T : ViewModel> Screen.getViewModel(
    viewModelProviderFactory: ViewModelProvider.Factory? = null,
): T {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val defaultViewModelProviderFactory = LocalViewModelProviderFactory.current

    return remember(key1 = T::class) {
        val hasDefaultViewModelProviderFactory =
            requireNotNull(lifecycleOwner as? HasDefaultViewModelProviderFactory) {
                "$lifecycleOwner is not a androidx.lifecycle.HasDefaultViewModelProviderFactory"
            }
        val viewModelStore = requireNotNull(viewModelStoreOwner.viewModelStore) {
            "$viewModelStoreOwner is null or have a null viewModelStore"
        }

        // val factory = VoyagerHiltViewModelFactories.getVoyagerFactory(
        //     activity = context.componentActivity,
        //     delegateFactory = viewModelProviderFactory
        //         ?: hasDefaultViewModelProviderFactory.defaultViewModelProviderFactory
        // )

        val provider = ViewModelProvider.create(
            store = viewModelStore,
            factory = viewModelProviderFactory ?: defaultViewModelProviderFactory,
            extras = hasDefaultViewModelProviderFactory.defaultViewModelCreationExtras,
        )
        provider[T::class]
    }
}


/**
 * A function to provide a [dagger.hilt.android.lifecycle.HiltViewModel] managed by Voyager ViewModelLifecycleOwner
 * instead of using Activity ViewModelLifecycleOwner.
 * There is compatibility with Activity ViewModelLifecycleOwner too but it must be avoided because your ViewModels
 * will be cleared when activity is totally destroyed only.
 *
 * @param viewModelProviderFactory A custom factory commonly used with Assisted Injection
 * @param viewModelFactory A custom factory to assist with creation of ViewModels
 * @return A new instance of [ViewModel] or the existent instance in the [ViewModelStore]
 */
@Composable
public inline fun <reified VM : ViewModel, F> Screen.getViewModel(
    viewModelProviderFactory: ViewModelProvider.Factory? = null,
    noinline viewModelFactory: (F) -> VM,
): VM {
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }

    val defaultViewModelProviderFactory = LocalViewModelProviderFactory.current

    return remember(key1 = VM::class) {
        val hasDefaultViewModelProviderFactory =
            requireNotNull(lifecycleOwner as? HasDefaultViewModelProviderFactory) {
                "$lifecycleOwner is not a androidx.lifecycle.HasDefaultViewModelProviderFactory"
            }
        val viewModelStore = requireNotNull(viewModelStoreOwner.viewModelStore) {
            "$viewModelStoreOwner is null or have a null viewModelStore"
        }

        val creationExtras = hasDefaultViewModelProviderFactory.defaultViewModelCreationExtras
            .withCreationCallback(viewModelFactory)

        // val factory = VoyagerHiltViewModelFactories.getVoyagerFactory(
        //     activity = context.componentActivity,
        //     delegateFactory = viewModelProviderFactory
        //         ?: hasDefaultViewModelProviderFactory.defaultViewModelProviderFactory
        // )

        val provider = ViewModelProvider.create(
            store = viewModelStore,
            factory = viewModelProviderFactory ?: defaultViewModelProviderFactory,
            extras = creationExtras,
        )

        provider[VM::class]
    }
}

fun <VMF : ViewModelFactory> CreationExtras.withCreationCallback(callback: (VMF) -> ViewModel): CreationExtras =
    MutableCreationExtras(this).addCreationCallback(callback)

@Suppress("UNCHECKED_CAST")
private fun <VMF : ViewModelFactory> MutableCreationExtras.addCreationCallback(callback: (VMF) -> ViewModel): CreationExtras =
    this.apply {
        this[CREATION_CALLBACK_KEY] = { factory -> callback(factory as VMF) }
    }

private val CREATION_CALLBACK_KEY: CreationExtras.Key<(ViewModelFactory) -> ViewModel> =
    object : CreationExtras.Key<(ViewModelFactory) -> ViewModel> {}

internal class KotlinInjectViewModelProviderFactory(
    private val viewModelMaps: Map<ViewModelKey, ViewModelCreator>,
    private val viewModelFactoryMaps: Map<ViewModelKey, ViewModelFactory>,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (viewModelMaps.containsKey(modelClass)) {
            return viewModelMaps[modelClass]!!() as T
        } else if (viewModelFactoryMaps.containsKey(modelClass)) {
            val callback: (ViewModelFactory) -> ViewModel = extras[CREATION_CALLBACK_KEY]!!
            return callback(viewModelFactoryMaps[modelClass]!!) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
        }
    }
}