package com.zhangke.utopia.pages

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.VoyagerHiltViewModelFactories
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.Navigator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.lifecycle.withCreationCallback
import kotlinx.coroutines.launch

class TabTestScreen : Screen {

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val tabs: List<Screen> = remember {
            listOf(
                FooScreen(0),
                FooScreen(1),
                BarScreen(2),
            )
        }
        val coroutineScope = rememberCoroutineScope()
//        TabNavigator(
//            tab = tabs.first(),
//            disposeNestedNavigators = true,
//        ) { tabNavigator ->
        Column(modifier = Modifier.fillMaxSize()) {
            val pagerState = rememberPagerState {
                tabs.size
            }
//                LaunchedEffect(pagerState.currentPage) {
//                    tabNavigator.current = tabs[pagerState.currentPage]
//                }
            TabRow(
                modifier = Modifier
                    .fillMaxWidth(),
                selectedTabIndex = pagerState.currentPage,
            ) {
                tabs.forEachIndexed { index, item ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.scrollToPage(index)
                            }
                        },
                    ) {
                        Box(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        ) {
                            Text(text = "$index")
                        }
                    }
                }
            }
            HorizontalPager(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1F),
                state = pagerState,
            ) { pageIndex ->
                Navigator(tabs[pageIndex])
//                    Box(modifier = Modifier.fillMaxSize()) {
//                        tabs[pageIndex].Content()
//                    }
            }
//            }
        }
    }
}

class FooScreen(private val pageIndex: Int) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel: FooViewModel = getViewModel<FooViewModel, FooViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "$pageIndex$pageIndex")
            }
        }
    }
}

@HiltViewModel(assistedFactory = FooViewModel.Factory::class)
class FooViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): FooViewModel
    }

    init {
        Log.d("U_TEST", "FooViewModel@${hashCode()} init, page index is $pageIndex")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("U_TEST", "FooViewModel@${hashCode()} onCleared")
    }
}

class BarScreen(private val pageIndex: Int) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val viewModel: BarViewModel = getViewModel<BarViewModel, BarViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(60.dp)
            ) {
                Text(text = "$pageIndex$pageIndex")
            }
        }
    }
}

@HiltViewModel(assistedFactory = BarViewModel.Factory::class)
class BarViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): BarViewModel
    }

    init {
        Log.d("U_TEST", "BarViewModel@${hashCode()} init, page index is $pageIndex")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("U_TEST", "BarViewModel@${hashCode()} onCleared")
    }
}

@Composable
@ExperimentalVoyagerApi
public inline fun <reified VM : ViewModel, F> Screen.getViewModelTest(
    viewModelProviderFactory: ViewModelProvider.Factory? = null,
    additionKey: Any? = null,
    noinline viewModelFactory: (F) -> VM,
): VM {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalViewModelStoreOwner"
    }
    return remember(key1 = VM::class) {
        val hasDefaultViewModelProviderFactory =
            requireNotNull(lifecycleOwner as? HasDefaultViewModelProviderFactory) {
                "$lifecycleOwner is not a androidx.lifecycle.HasDefaultViewModelProviderFactory"
            }
        val viewModelStore = requireNotNull(viewModelStoreOwner?.viewModelStore) {
            "$viewModelStoreOwner is null or have a null viewModelStore"
        }

        val creationExtras = hasDefaultViewModelProviderFactory.defaultViewModelCreationExtras
            .withCreationCallback(viewModelFactory)

        val factory = VoyagerHiltViewModelFactories.getVoyagerFactory(
            activity = context.componentActivity,
            delegateFactory = viewModelProviderFactory
                ?: hasDefaultViewModelProviderFactory.defaultViewModelProviderFactory
        )

        val provider = ViewModelProvider(
            store = viewModelStore,
            factory = factory,
            defaultCreationExtras = creationExtras
        )

        provider[VM::class.java]
    }
}

private inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}

val Context.componentActivity: ComponentActivity
    get() = findOwner<ComponentActivity>(this)
        ?: error("Context must be a androidx.activity.ComponentActivity. Current is $this")