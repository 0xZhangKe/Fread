package com.zhangke.fread.bluesky.internal.screen.user.edit

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.fread.common.page.BaseScreen
import com.zhangke.fread.status.model.IdentityRole

class EditProfileScreen(
    private val role: IdentityRole,
) : BaseScreen() {

    @Composable
    override fun Content() {
        super.Content()

        val viewModel = getViewModel<EditProfileViewModel, EditProfileViewModel.Factory> {
            it.create(role)
        }
    }
}
