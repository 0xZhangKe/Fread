package com.zhangke.utopia.pages

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.ktx.launchInViewModel
import com.zhangke.framework.voyager.LocalGlobalNavigator
import com.zhangke.utopia.pages.main.MainPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UtopiaScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalGlobalNavigator provides LocalNavigator.currentOrThrow
        ) {
            MainPage()
        }

//        val navigator = LocalNavigator.currentOrThrow
//        LaunchedEffect(Unit) {
//            navigator.push(TabTestScreen())
//        }

//        val viewModel = getViewModel<TestViewModel>()
//        val list by viewModel.list.collectAsState()
//        val state = rememberLazyListState()
//        val layoutInfo by remember { derivedStateOf { state.layoutInfo } }
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            state = state,
//        ) {
//            items(
//                items = list,
//                key = {
//                    it
//                }
//            ) { item ->
//                Box(
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .fillMaxWidth()
//                        .height(40.dp)
//                ) {
//                    Card(
//                        modifier = Modifier
//                            .fillMaxSize()
//                    ) {
//                        Box(modifier = Modifier.fillMaxSize()) {
//                            Text(
//                                modifier = Modifier.align(Alignment.Center),
//                                text = item
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }
}

//class TestViewModel : ViewModel() {
//
//    private val _list = MutableStateFlow<List<String>>(emptyList())
//    val list: StateFlow<List<String>> = _list.asStateFlow()
//
//    init {
//        launchInViewModel {
//            var header = 1000
//            var tail = 1010
//            while (true) {
//                val currentList = _list.value.toMutableList()
//                Log.d("U_TEST", currentList.joinToString(","))
//                repeat(10) {
//                    currentList.add(0, "${header--}")
//                }
//                repeat(10) {
//                    currentList += "${tail++}"
//                }
//                _list.value = currentList
//                delay(1000)
//            }
//        }
//    }
//}


