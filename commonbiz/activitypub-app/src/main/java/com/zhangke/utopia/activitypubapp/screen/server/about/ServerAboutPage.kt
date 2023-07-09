package com.zhangke.utopia.activitypubapp.screen.server.about

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.utopia.activitypubapp.model.ActivityPubInstanceRule

@Composable
internal fun Screen.ServerAboutPage(
    rules: List<ActivityPubInstanceRule> = emptyList(),
    contentCanScrollBackward: MutableState<Boolean>,
) {
    val viewModel: ServerAboutViewModel = getViewModel()
    viewModel.rules = rules
    LaunchedEffect(viewModel){
        viewModel.onPageResume()
    }

    ServerAboutPageContent()
}

@Composable
private fun ServerAboutPageContent(
    uiState: ServerAboutUiState,
) {

}
