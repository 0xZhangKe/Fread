package com.zhangke.utopia.pages

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.utopia.pages.main.MainPage
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

class UtopiaScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalGlobalNavigator provides LocalNavigator.currentOrThrow
        ) {
            MainPage()
//            Navigator(TabTestScreen())
        }

//        val tabs: List<PagerTab> = remember {
//            listOf(
//                FirstTab(0),
//                FirstTab(1),
//                SecondTab(2),
//                ThirdTab(3),
//            )
//        }
//        val pagerState = rememberPagerState {
//            tabs.size
//        }
//            Column(modifier = Modifier.fillMaxSize()) {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    Button(onClick = {
//                        tabNavigator.current = tabs.first()
//                    }) {
//                        Text(text = "First")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[1]
//                    }) {
//                        Text(text = "Second")
//                    }
//                    Button(onClick = {
//                        tabNavigator.current = tabs[2]
//                    }) {
//                        Text(text = "Third")
//                    }
//                }
//                Box(modifier = Modifier.fillMaxSize()) {
//                    CurrentTab()
//                }
//            }
//        HorizontalPager(
//            modifier = Modifier.fillMaxSize(),
//            state = pagerState,
//        ) {
//            Log.d("U_TEST", "current page index is $it")
//            with(tabs[it]) {
//                TabContent()
//            }
//        }
    }
}

interface PagerTab {

    val title: String
        @Composable get

    @Composable
    fun Screen.TabContent()
}

class FirstTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<FirstViewModel, FirstViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
            )
        }
    }
}

@HiltViewModel(assistedFactory = FirstViewModel.Factory::class)
class FirstViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): FirstViewModel
    }

    init {
        Log.d("U_TEST", "FirstViewModel@${hashCode()} init, index is $pageIndex")
    }
}


class SecondTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: SecondViewModel = getViewModel<SecondViewModel, SecondViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Second",
            )
        }
    }
}

@HiltViewModel(assistedFactory = SecondViewModel.Factory::class)
class SecondViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): SecondViewModel
    }

    init {
        Log.d("U_TEST", "SecondViewModel@${hashCode()} init, index is $pageIndex")
    }
}


class ThirdTab(private val pageIndex: Int) : PagerTab {

    override val title: String
        @Composable get() = "$pageIndex"

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel: ThirdViewModel = getViewModel<ThirdViewModel, ThirdViewModel.Factory> {
            it.create(pageIndex)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Third",
            )
        }
    }
}
@HiltViewModel(assistedFactory = ThirdViewModel.Factory::class)
class ThirdViewModel @AssistedInject constructor(
    @Assisted val pageIndex: Int,
) : ViewModel() {

    @AssistedFactory
    interface Factory : ScreenModelFactory {
        fun create(pageIndex: Int): ThirdViewModel
    }

    init {
        Log.d("U_TEST", "ThirdViewModel@${hashCode()} init, index is $pageIndex")
    }
}

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class HiltModule {
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(FirstViewModel.Factory::class)
//    abstract fun bindFirstScreenModelFactory(
//        hiltDetailsScreenModelFactory: FirstViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(FirstViewModel::class)
//    abstract fun bindFirstScreenModel(testScreenModel: FirstViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(SecondViewModel.Factory::class)
//    abstract fun bindSecondScreenModelFactory(
//        hiltDetailsScreenModelFactory: SecondViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(SecondViewModel::class)
//    abstract fun bindSecondScreenModel(testScreenModel: SecondViewModel): ScreenModel
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(ThirdViewModel.Factory::class)
//    abstract fun bindThirdScreenModelFactory(
//        hiltDetailsScreenModelFactory: ThirdViewModel.Factory
//    ): ScreenModelFactory
//
//    @Binds
//    @IntoMap
//    @ScreenModelKey(ThirdViewModel::class)
//    abstract fun bindThirdScreenModel(testScreenModel: ThirdViewModel): ScreenModel
//}
