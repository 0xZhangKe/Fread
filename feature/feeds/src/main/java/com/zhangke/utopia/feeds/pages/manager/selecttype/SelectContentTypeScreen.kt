package com.zhangke.utopia.feeds.pages.manager.selecttype

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.rememberSnackbarHostState
import com.zhangke.framework.voyager.navigationResult
import com.zhangke.framework.voyager.pushDestination
import com.zhangke.utopia.common.ext.nameResId
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.pages.manager.add.AddFeedsManagerScreen
import com.zhangke.utopia.status.model.ContentConfig
import com.zhangke.utopia.status.model.ContentType

class SelectContentTypeScreen : Screen {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SelectContentTypeViewModel = getViewModel()
        var lastItemKey: String? by rememberSaveable {
            mutableStateOf(null)
        }
        ConsumeFlow(viewModel.openPageFlow) {
            navigator.pushDestination(it)
            lastItemKey = navigator.lastItem.key
        }
        if (lastItemKey != null) {
            val contentConfig by navigator.navigationResult
                .getResult<ContentConfig>(lastItemKey!!)
            if (contentConfig != null) {
                LaunchedEffect(contentConfig) {
                    viewModel.onConfigAdd(contentConfig!!)
                }
            }
        }
        val snackbarState = rememberSnackbarHostState()
        ConsumeFlow(viewModel.addContentSuccessFlow) {
            snackbarState.showSnackbar(context.getString(R.string.add_content_success_snackbar))
            navigator.pop()
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.select_feeds_type_screen_title),
                    onBackClick = navigator::pop,
                )
            },
            snackbarHost = {
                SnackbarHost(snackbarState)
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val typeList = remember {
                    ContentType.entries
                }
                typeList.forEachIndexed { index, type ->
                    TextButton(
                        onClick = {
                            when (type) {
                                ContentType.MIXED -> {
                                    navigator.push(AddFeedsManagerScreen())
                                    lastItemKey = navigator.lastItem.key
                                }

                                ContentType.ACTIVITY_PUB -> {
                                    viewModel.onTypeClick(type)
                                }
                            }
                        },
                    ) {
                        Text(text = stringResource(type.nameResId))
                    }
                    if (index < typeList.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(16.dp)
                        )
                    }
                }
            }
        }
    }
}
