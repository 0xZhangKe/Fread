package com.zhangke.utopia.feeds.pages.manager.selecttype

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.framework.composable.ConsumeFlow
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.voyager.tryPush
import com.zhangke.utopia.common.ext.nameResId
import com.zhangke.utopia.feeds.R
import com.zhangke.utopia.feeds.pages.manager.add.AddFeedsManagerScreen
import com.zhangke.utopia.status.model.StatusProviderType

class SelectFeedsTypeScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: SelectFeedsTypeViewModel = getViewModel()
        ConsumeFlow(viewModel.openPageFlow) {
            navigator.tryPush(it)
        }
        Scaffold(
            topBar = {
                Toolbar(
                    title = stringResource(R.string.select_feeds_type_screen_title),
                    onBackClick = navigator::pop,
                )
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
                    StatusProviderType.entries
                }
                typeList.forEachIndexed { index, type ->
                    TextButton(
                        onClick = {
                            when (type) {
                                StatusProviderType.MIXED -> {
                                    navigator.push(AddFeedsManagerScreen())
                                }

                                StatusProviderType.ACTIVITY_PUB -> {
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
