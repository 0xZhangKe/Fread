package com.zhangke.utopia.activitypub.app.internal.screen.user.about

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.hilt.getViewModel
import com.zhangke.activitypub.entities.ActivityPubField
import com.zhangke.framework.composable.PagerTab
import com.zhangke.framework.composable.PagerTabOptions
import com.zhangke.framework.composable.text.RichText
import com.zhangke.utopia.activitypub.app.R
import com.zhangke.utopia.activitypub.app.internal.model.UserUriInsights

class UserAboutTab(
    private val contentCanScrollBackward: MutableState<Boolean>,
    private val userUriInsights: UserUriInsights,
) : PagerTab {

    override val options: PagerTabOptions
        @Composable get() = PagerTabOptions(
            title = stringResource(R.string.activity_pub_user_detail_tab_about)
        )

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Screen.TabContent() {
        val viewModel = getViewModel<UserAboutViewModel, UserAboutViewModel.Factory>() {
            it.create(userUriInsights)
        }
        val uiState by viewModel.uiState.collectAsState()
        UserAboutContent(
            uiState = uiState,
        )
    }

    @Composable
    private fun UserAboutContent(
        uiState: UserAboutUiState
    ) {
        val scrollState = rememberScrollState()
        contentCanScrollBackward.value = scrollState.value > 0
        Column(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
                .padding(16.dp)
        ) {
            if (!uiState.joinedDatetime.isNullOrEmpty()) {
                Text(
                    text = stringResource(
                        R.string.activity_pub_user_detail_tab_about_joined,
                        uiState.joinedDatetime
                    ),
                )
            }

            for (field in uiState.fieldList) {
                FieldUi(
                    field = field,
                )
            }
        }
    }

    @Composable
    private fun FieldUi(
        field: ActivityPubField,
    ) {
        Text(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            text = field.name,
        )
        RichText(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxWidth(),
            text = field.value,
        )
    }
}
