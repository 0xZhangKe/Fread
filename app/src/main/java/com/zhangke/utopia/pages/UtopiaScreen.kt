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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class UtopiaScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
//        CompositionLocalProvider(
//            LocalGlobalNavigator provides LocalNavigator.currentOrThrow
//        ) {
//            MainPage()
//        }

        val tabs = remember {
            listOf(
                FirstTab(),
                SecondTab(),
                ThirdTab(),
            )
        }
        TabNavigator(tabs.first()) {
            val pagerState = rememberPagerState {
                tabs.size
            }
            val tabNavigator = LocalTabNavigator.current
            LaunchedEffect(pagerState.currentPage) {
                Log.d("U_TEST", "currentPage has changed to ${pagerState.currentPage}")
                tabNavigator.current = tabs[pagerState.currentPage]
            }
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
            HorizontalPager(
                modifier = Modifier.fillMaxSize(),
                state = pagerState,
            ) {
                Log.d("U_TEST", "current page index is $it")
                CurrentTab()
            }
        }
    }
}

class FirstTab : Tab {

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 0.toUShort(),
            title = "First",
            icon = null,
        )

    @Composable
    override fun Content() {
        val viewModel: FirstViewModel = getViewModel()
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "First",
            )
        }
    }
}

@HiltViewModel
class FirstViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("U_TEST", "FirstViewModel@${hashCode()} init")
    }
}


class SecondTab : Tab {

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 1.toUShort(),
            title = "Second",
            icon = null,
        )

    @Composable
    override fun Content() {
        val viewModel: SecondViewModel = getViewModel()
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Second",
            )
        }
    }
}

@HiltViewModel
class SecondViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("U_TEST", "SecondViewModel@${hashCode()} init")
    }
}


class ThirdTab : Tab {

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 2.toUShort(),
            title = "Third",
            icon = null,
        )

    @Composable
    override fun Content() {
        val viewModel: ThirdViewModel = getViewModel()
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "Third",
            )
        }
    }
}

@HiltViewModel
class ThirdViewModel @Inject constructor() : ViewModel() {

    init {
        Log.d("U_TEST", "ThirdViewModel@${hashCode()} init")
    }
}
