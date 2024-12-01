package com.zhangke.fread.bluesky.internal.screen.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.framework.composable.Toolbar
import com.zhangke.framework.composable.stringResource
import com.zhangke.framework.network.FormalBaseUrl
import com.zhangke.fread.bluesky.Res
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.krouter.annotation.Destination
import org.jetbrains.compose.resources.stringResource

@Destination(AddBlueskyContentRoute.ROUTE)
class AddBlueskyContentScreen(private val baseUrl: FormalBaseUrl) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel =
            getViewModel<AddBlueskyContentViewModel, AddBlueskyContentViewModel.Factory> {
                it.create(baseUrl)
            }
    }

    @Composable
    private fun AddBlueskyContentContent(
        onBackClick: () -> Unit,
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Toolbar(
                    title = stringResource(Res.string.bsky_add_content_title),
                    onBackClick = onBackClick,
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {

            }
        }
    }
}
