package com.zhangke.utopia.pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.hilt.ScreenModelFactory
import cafe.adriel.voyager.hilt.ScreenModelFactoryKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.zhangke.framework.composable.UtopiaTabRow
import dagger.Binds
import dagger.Module
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.multibindings.IntoMap
import kotlinx.coroutines.launch
import javax.inject.Inject

//class TabTestScreen : AndroidScreen() {
//
//    @Composable
//    override fun Content() {
//        val tabList = remember {
//            mutableListOf<Tab>().also { list ->
//                repeat(10) {
//                    list += TestTab(it)
//                }
//            }
//        }
//        var currentIndex by remember {
//            mutableIntStateOf(0)
//        }
//        val tabCount = 10
//        TabNavigator(
//            tab = tabList.first()
//        ) {
//            val tabNavigator = LocalTabNavigator.current
//            LaunchedEffect(currentIndex) {
//                tabNavigator.current = tabList[currentIndex]
//            }
//            Column(modifier = Modifier.fillMaxSize()) {
//                UtopiaTabRow(
//                    modifier = Modifier.fillMaxWidth(),
//                    selectedTabIndex = currentIndex,
//                    selectedIndex = currentIndex,
//                    tabCount = tabCount,
//                    tabContent = { index ->
//                        Box(
//                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
//                        ) {
//                            Text(
//                                text = "$index tab",
//                            )
//                        }
//                    },
//                    onTabClick = { index ->
//                        currentIndex = index
//                    }
//                )
//                Box(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .weight(1F)
//                ) {
//                    CurrentTab()
//                }
//            }
//        }
//    }
//}

//class TestTab(private val tabIndex: Int) : Tab {
//
//    override val options: TabOptions
//        @Composable get() {
//            val icon = rememberVectorPainter(Icons.Default.Home)
//            return remember {
//                TabOptions(
//                    index = 0u, title = "Home", icon = icon
//                )
//            }
//        }

//    @Composable
//    override fun Content() {
//        val viewModel = getScreenModel<TestViewModel, TestViewModel.Factory> { factory ->
//            factory.create(tabIndex)
//        }
//        viewModel.tabIndex = tabIndex
//        LaunchedEffect(viewModel, tabIndex) {
//            Log.d("U_TEST", "Tab is $tabIndex")
//            viewModel.onPrepared()
//        }
//        Box(modifier = Modifier.fillMaxSize()) {
//            Text(
//                modifier = Modifier.align(Alignment.Center),
//                text = "$tabIndex",
//            )
//        }
//    }
//}

//class TestViewModel @AssistedInject constructor(
//    @Assisted val index: Int
//) : ScreenModel {
//
//    @AssistedFactory
//    interface Factory : ScreenModelFactory {
//        fun create(index: Int): TestViewModel
//    }
//
//    var tabIndex: Int = -1
//
//    fun onPrepared() {
//        Log.d("U_TEST", "TestViewModel@${hashCode()} init index:$tabIndex")
//    }
//
//    override fun onDispose() {
//        super.onDispose()
//        Log.d("U_TEST", "TestViewModel@${hashCode()} onCleared index:$tabIndex")
//    }
//}

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class HiltModule {
//
//    @Binds
//    @IntoMap
//    @ScreenModelFactoryKey(TestViewModel.Factory::class)
//    abstract fun bindHiltDetailsScreenModelFactory(
//        hiltDetailsScreenModelFactory: TestViewModel.Factory
//    ): ScreenModelFactory

//    @Binds
//    @IntoMap
//    @ScreenModelKey(TestViewModel::class)
//    abstract fun bindHiltScreenModel(testScreenModel: TestViewModel): ScreenModel
//}
