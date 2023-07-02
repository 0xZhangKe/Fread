package com.zhangke.utopia.feeds.pages.manager

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.zhangke.framework.ktx.CollectOnComposable
import com.zhangke.utopia.feeds.feedsModuleRoute
import com.zhangke.utopia.feeds.pages.manager.search.SearchSourceForAddPageRoute

internal class FeedsManagerRoute {

    val route = "$feedsModuleRoute/manager?type={type}&addUris={addUris}"
}

internal fun NavGraphBuilder.feedsManagerRoute(navController: NavController) {
    composable(
        route = FeedsManagerRoute().route,
        arguments = listOf(
            navArgument("type") { defaultValue = "" },
            navArgument("addUris") { defaultValue = "" },
        ),
    ) {
        val viewModel: FeedsManagerViewModel = hiltViewModel()
        val addUris = it.arguments?.getString("addUris")
        if (addUris.isNullOrEmpty().not()) {
            LaunchedEffect(addUris) {
                viewModel.onAddSources(addUris!!)
            }
        }
        FeedsManagerPage(
            uiState = viewModel.uiState.collectAsState().value,
            errorMessageFlow = viewModel.errorMessageFlow,
            onAddSourceClick = {
                navController.navigate(SearchSourceForAddPageRoute().route)
            },
            onConfirmClick = viewModel::onConfirmClick,
            onNameInputValueChanged = viewModel::onSourceNameInput,
            onRemoveSourceClick = viewModel::onRemoveSource,
            onChooseSourceItemClick = viewModel::onAuthItemClick,
            onChooseSourceDialogDismissRequest = viewModel::onChooseDialogDismissRequest,
        )
        viewModel.finishPage.CollectOnComposable {
            navController.popBackStack()
        }
    }
}
