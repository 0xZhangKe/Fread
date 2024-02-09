package com.zhangke.utopia.activitypub.app.internal.screen.account

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.zhangke.krouter.Destination
import com.zhangke.krouter.Router

@Destination(EditAccountInfoRoute.ROUTE)
class EditAccountInfoScreen(
    @Router private val route: String = "",
) : Screen {

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getViewModel<EditAccountInfoViewModel, EditAccountInfoViewModel.Factory>() {
            it.create(EditAccountInfoRoute.parseRoute(route))
        }
        val uiState by viewModel.uiState.collectAsState()
        EditAccountInfoContent(
            uiState = uiState,
            onBackClick = navigator::pop,
        )
    }

    @Composable
    private fun EditAccountInfoContent(
        uiState: EditAccountUiState,
        onBackClick: () -> Unit,
    ) {

    }
}
