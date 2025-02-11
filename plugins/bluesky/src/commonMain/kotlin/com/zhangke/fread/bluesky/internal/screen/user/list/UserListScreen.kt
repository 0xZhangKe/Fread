package com.zhangke.fread.bluesky.internal.screen.user.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class UserListScreen(
    private val role: IdentityRole,
    private val type: UserListType,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()
        val viewModel = getViewModel<UserListViewModel, UserListViewModel.Factory>() {
            it.create(role, type)
        }
        val uiState by viewModel.uiState.collectAsState()

    }
}
